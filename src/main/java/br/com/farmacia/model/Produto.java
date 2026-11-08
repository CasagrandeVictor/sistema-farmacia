package br.com.farmacia.model;

/**
 * Entidade que representa um produto vendido na farmácia.
 *
 * Um produto tem nome, preço unitário e um indicador de se exige receita
 * (medicamentos controlados). É uma classe simples de dados (entidade).
 */
public class Produto {

    private final String nome;
    private final double preco;
    private final boolean exigeReceita;

    public Produto(String nome, double preco, boolean exigeReceita) {
        this.nome = nome;
        this.preco = preco;
        this.exigeReceita = exigeReceita;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public boolean isExigeReceita() {
        return exigeReceita;
    }

    @Override
    public String toString() {
        String tag = exigeReceita ? " (exige receita)" : "";
        return String.format("%s - R$ %.2f%s", nome, preco, tag);
    }
}
