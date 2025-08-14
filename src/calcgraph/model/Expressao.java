package calcgraph.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "expressao")
public class Expressao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_expressao")
    private Long id;

    @Lob
    @Column(name = "entrada", nullable = false)
    private String entrada;

    @Lob
    @Column(name = "resultado", nullable = false)
    private String resultado;

    // Preenchido pelo DEFAULT CURRENT_TIMESTAMP do MySQL
    @Column(name = "data_criacao", insertable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "qntd_variaveis", nullable = false)
    private Integer quantidadeVariaveis;

    @OneToOne(mappedBy = "expressao", cascade = CascadeType.ALL,
              orphanRemoval = true, fetch = FetchType.LAZY)
    private Favorito favorito;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEntrada() { return entrada; }
    public void setEntrada(String entrada) { this.entrada = entrada; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public Integer getQuantidadeVariaveis() { return quantidadeVariaveis; }
    public void setQuantidadeVariaveis(Integer quantidadeVariaveis) { this.quantidadeVariaveis = quantidadeVariaveis; }
    public Favorito getFavorito() { return favorito; }
    public void setFavorito(Favorito favorito) { this.favorito = favorito; }
}
