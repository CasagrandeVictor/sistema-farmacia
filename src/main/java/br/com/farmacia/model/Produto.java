package br.com.farmacia.model;

import br.com.farmacia.util.Moeda;

import java.math.BigDecimal;

/**
 * Entidade que representa um produto vendido na farmácia.
 *
 * Um produto tem nome, preço unitário e um indicador de se exige receita
 * (medicamentos controlados). É uma classe simples de dados (entidade).
 */
public class Produto {

    private final String nome;
    private final BigDecimal preco;
    private final boolean exigeReceita;

    public Produto(String nome, BigDecimal preco, boolean exigeReceita) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório.");
        }
        if (preco == null || preco.signum() <= 0) {
            throw new IllegalArgumentException("Preço do produto deve ser maior que zero.");
        }
        this.nome = nome;
        this.preco = preco;
        this.exigeReceita = exigeReceita;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public boolean isExigeReceita() {
        return exigeReceita;
    }

    @Override
    public String toString() {
        String tag = exigeReceita ? " (exige receita)" : "";
        return String.format("%s - %s%s", nome, Moeda.formatar(preco), tag);
    }
}
