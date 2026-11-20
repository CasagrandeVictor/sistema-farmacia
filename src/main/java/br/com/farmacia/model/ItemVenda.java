package br.com.farmacia.model;

import br.com.farmacia.util.Moeda;

import java.math.BigDecimal;

/**
 * Representa um item dentro de uma venda: um produto e a quantidade comprada.
 */
public class ItemVenda {

    private final Produto produto;
    private final int quantidade;

    public ItemVenda(Produto produto, int quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Item da venda precisa de um produto.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    /** Preço do item = preço unitário x quantidade. */
    public BigDecimal getSubtotal() {
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }

    @Override
    public String toString() {
        return String.format("%dx %s = %s",
                quantidade, produto.getNome(), Moeda.formatar(getSubtotal()));
    }
}
