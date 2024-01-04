package br.com.alura.screenmatch.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacoes;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

    public Serie() {
    }

    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacoes = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacoes())).orElse(0);
        this.genero = Categoria.fromPortugues(dadosSerie.generos().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
//        this.sinopse = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse().trim());    -> ESSA FUNÇÃO UTILIZA O CHATGPT PARA TRADUZIR A SINOPSE
        this.sinopse = dadosSerie.sinopse();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(Double avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return
                " genero =" + genero +
                        ", titulo ='" + titulo + '\'' +
                        ", totalTemporadas =" + totalTemporadas +
                        ", avaliacoes =" + avaliacoes +
                        ", atores ='" + atores + '\'' +
                        ", sinopse ='" + sinopse + '\'' +
                        ", episodios ='" + episodios + '\'';
    }
}
