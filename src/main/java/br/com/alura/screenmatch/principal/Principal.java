package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSerieList = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> serieList = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {

        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    \n
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Bucar série por atores
                    6 - Buscar top 5 séries                
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    bucarTopCincoSeries();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void listarSeriesBuscadas() {
        serieList = repositorio.findAll();
        serieList.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        dadosSerieList.add(dados);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("\nDigite uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = serieList.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DadosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporadas dadosTemporada = conversor.obterDados(json, DadosTemporadas.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("\nSérie não encontrada");
        }
    }

    private void buscarSeriePorTitulo() {
        System.out.println("\nDigite uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serieBuscada.isPresent()){
            System.out.println("Dados da serie: " + serieBuscada.get());
        }else{
            System.out.println("Serie não encontrada");
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome de um ator: ");
        var nomeAtor = leitura.nextLine();
        System.out.println("A partir de que avaliação: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesBuscadas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacoesGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Series em que " + nomeAtor + "trabalhou: ");
        seriesBuscadas.forEach(s -> System.out.println(s.getTitulo() + "| Avaliação: " + s.getAvaliacoes()));
        }

    private void bucarTopCincoSeries() {
        List<Serie> topCinco = repositorio.findTop7ByOrderByAvaliacoesDesc();
        topCinco.forEach(s -> System.out.println(s.getTitulo() + "| Avaliação: " + s.getAvaliacoes()));
    }


}






