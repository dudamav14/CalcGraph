package calcgraph.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorito",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_expressao"}))
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorito")
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_expressao", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_favorito_expressao"))
    private Expressao expressao;

    @Column(name = "apelido", length = 100)
    private String apelido;

    @Column(name = "descricao", length = 100)
    private String descricao;

    // Preenchido pelo DEFAULT CURRENT_TIMESTAMP
    @Column(name = "data_favorito", insertable = false, updatable = false)
    private LocalDateTime dataFavorito;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Expressao getExpressao() { return expressao; }
    public void setExpressao(Expressao expressao) { this.expressao = expressao; }
    public String getApelido() { return apelido; }
    public void setApelido(String apelido) { this.apelido = apelido; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataFavorito() { return dataFavorito; }
}
