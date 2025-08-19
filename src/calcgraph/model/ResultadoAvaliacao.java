/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calcgraph.model;

/**
 *
 * @author Caio
 */
public class ResultadoAvaliacao {

    private TipoResultado tipo;
    private Double valor;
    private String funcao;

    boolean isGrafico() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Object getExpressao() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public enum TipoResultado {
        NUMERICO,
        GRAFICO
    }

    // Construtor para resultados numéricos
    public ResultadoAvaliacao(Double valor) {
        this.tipo = TipoResultado.NUMERICO;
        this.valor = valor;
    }

    // Construtor para resultados de função (gráficos)
    public ResultadoAvaliacao(String funcao) {
        this.tipo = TipoResultado.GRAFICO;
        this.funcao = funcao;
    }

    // Getters para os campos
    public TipoResultado getTipo() {
        return tipo;
    }

    public Double getValor() {
        return valor;
    }

    public String getFuncao() {
        return funcao;
    }
}
