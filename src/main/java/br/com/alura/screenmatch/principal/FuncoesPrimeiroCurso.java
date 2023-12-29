package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodios;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporadas;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FuncoesPrimeiroCurso {
    private static final String ENDERECO ="https://www.omdbapi.com/?t=";
    private static final String API_KEY = "&apikey=4181aeb";

    Scanner scanner = new Scanner(System.in);
    ConsumoAPI consumo = new ConsumoAPI();
    ConverteDados conversor = new ConverteDados();

    public void funcoes(){

        System.out.println("Digite uma serie que deseja informações: ");
        var nomeSerie = scanner.nextLine();

        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporadas> temporadasList = new ArrayList<>();
        for(int i = 1; i <= dadosSerie.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporadas dadosTemporada = conversor.obterDados(json, DadosTemporadas.class);
            temporadasList.add(dadosTemporada);
        }

        // FUNÇÃO LAMBDA PARA EXIBIR O TITULO DE TODOS OS EPS
        temporadasList.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        // FUNÇÃO PARA JUNTAR TODOS OS EPS DE CADA TEMP EM UMA LIST SÓ (FLATMAP)
        List<DadosEpisodios> todosEpisodios = temporadasList.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\n TOP 10 EPISODIOS DE " + nomeSerie);
        todosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())
                .limit(10)
                .forEach(System.out::println);

        // FUNÇÃO PARA CONVERTER CADA EPS DA LISTA EM UM OBJETO DA CLASSE EPS
        List<Episodio> episodioList = temporadasList.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        //FUNÇÃO PARA BUSCAR UM EPISODIO PELO TITULO
        System.out.println("Digite um trecho do eps: ");
        var tituloBuscado = scanner.nextLine();

        Optional<Episodio> episodioEncontrado = episodioList.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(tituloBuscado.toUpperCase()))
                .findFirst();

        if(episodioEncontrado.isPresent()){
            System.out.println("Episodio encontrado!\n" + episodioEncontrado.get());
        }else{
            System.out.println("Episodio não encontrado");
        }

        // FUNÇÃO PARA BUSCAR SÉRIES APENAS A PARTIR DE UMA CERTA DATA
        System.out.println("A partir de que ano deseja ver os episodioList: ");
        var ano = scanner.nextInt();
        scanner.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodioList.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episodio: " + e.getTitulo() +
                                " Data de lançamento: " + e.getDataLancamento().format(formatador)
                ));

        //FUNÇÃO QUE CRIA UM MAP COM AS AVALIAÇÕES POR TEMPORADA
        Map<Integer, Double> avaliacoesPorTemporada = episodioList.stream()
                .filter(e -> e.getAvaliacao() > 0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        //FUNÇÃO QUE GERA ESTATISTICAS SOBRE AVALIAÇÕES
        DoubleSummaryStatistics estatistics = episodioList.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + estatistics.getAverage());
        System.out.println("Melhor avaliação: " + estatistics.getMax());
        System.out.println("Pior avaliação: " + estatistics.getMin());
        System.out.println("Total de avaliações: " + estatistics.getCount());
    }
}





