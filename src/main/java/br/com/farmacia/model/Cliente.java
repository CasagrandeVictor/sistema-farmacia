package br.com.farmacia.model;

/**
 * Entidade que representa um cliente da farmácia.
 *
 * Guarda nome, idade e se o cliente possui cadastro no programa de
 * fidelidade. Esses dados são usados pelas políticas de desconto (Strategy)
 * para decidir qual desconto aplicar.
 */
public class Cliente {

    /** Maior idade aceita no cadastro. */
    public static final int IDADE_MAXIMA = 130;

    private final String nome;
    private final int idade;
    private final boolean fidelidade;
    private final boolean carteiraINSS;

    public Cliente(String nome, int idade, boolean fidelidade, boolean carteiraINSS) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }
        if (idade < 0 || idade > IDADE_MAXIMA) {
            throw new IllegalArgumentException(
                    "Idade deve estar entre 0 e " + IDADE_MAXIMA + " anos.");
        }
        this.nome = nome;
        this.idade = idade;
        this.fidelidade = fidelidade;
        this.carteiraINSS = carteiraINSS;
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

    public boolean hasCarteiraINSS() {
        return carteiraINSS;
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
