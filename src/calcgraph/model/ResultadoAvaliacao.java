/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calcgraph.model;

import calcgraph.model.token.TokenType;

/**
 *
 * @author Caio
 */
public class ResultadoAvaliacao {

    private TipoResultado tipo;
    private Double valor;
    private String binario;
    private String funcao;

    public enum TipoResultado {
        NUMERICO,
        GRAFICO,
        BINARIO
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
    
    public ResultadoAvaliacao(String binario, TipoResultado tipo) {
        this.tipo = tipo;
        this.binario = binario;
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
    
    public String getBinario() {
        return binario;
    }
}
