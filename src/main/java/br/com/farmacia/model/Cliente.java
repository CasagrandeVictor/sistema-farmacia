package br.com.farmacia.model;

/**
 * Entidade que representa um cliente da farmácia.
 *
 * Guarda nome, idade e se o cliente possui cadastro no programa de
 * fidelidade. Esses dados são usados pelas políticas de desconto (Strategy)
 * para decidir qual desconto aplicar.
 */
public class Cliente {

    private final String nome;
    private final int idade;
    private final boolean fidelidade;

    public Cliente(String nome, int idade, boolean fidelidade) {
        this.nome = nome;
        this.idade = idade;
        this.fidelidade = fidelidade;
    }

    public String getNome() {
        return nome;
    }

    public int getIdade() {
        return idade;
    }

    public boolean isFidelidade() {
        return fidelidade;
    }

    /** Considera idoso o cliente com 60 anos ou mais (Estatuto do Idoso). */
    public boolean isIdoso() {
        return idade >= 60;
    }

    @Override
    public String toString() {
        return String.format("%s (%d anos)%s",
                nome, idade, fidelidade ? " - cliente fidelidade" : "");
    }
}
