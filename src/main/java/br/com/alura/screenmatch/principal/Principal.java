package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodios;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporadas;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private static final String ENDERECO ="https://www.omdbapi.com/?t=";
    private static final String API_KEY = "&apikey=4181aeb";

    Scanner scanner = new Scanner(System.in);
    ConsumoAPI consumo = new ConsumoAPI();
    ConverteDados conversor = new ConverteDados();

    public void exibeMenu(){
        System.out.println("Digite uma serie que deseja informações: ");
        var nomeSerie = scanner.nextLine();

        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporadas> temporadas = new ArrayList<>();
        for(int i = 1; i <= dadosSerie.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporadas dadosTemporada = conversor.obterDados(json, DadosTemporadas.class);
            temporadas.add(dadosTemporada);
        }

        for (DadosTemporadas temporada : temporadas) {
            System.out.println(temporada);
        }

//        for(int i = 0; i < dadosSerie.totalTemporadas(); i++){
//            List<DadosEpisodios> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        // FUNÇÃO LAMBDA PARA ESCREVER O CÓDIGO ACIMA
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        // FUNÇÃO PARA JUNTAR TODOS OS EPS DE CADA TEMP EM UMA LIST SÓ (FLATMAP)
        List<DadosEpisodios> todosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        // FUNÇÃO PARA IMPRIMIR TOP 5 MELHORES EPS

        System.out.println("\n TOP 5 EPISODIOS DE " + nomeSerie);
        todosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

    }

}
