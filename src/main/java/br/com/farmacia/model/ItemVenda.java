package br.com.farmacia.model;

/**
 * Representa um item dentro de uma venda: um produto e a quantidade comprada.
 */
public class ItemVenda {

    private final Produto produto;
    private final int quantidade;

    public ItemVenda(Produto produto, int quantidade) {
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
    public double getSubtotal() {
        return produto.getPreco() * quantidade;
    }

    @Override
    public String toString() {
        return String.format("%dx %s = R$ %.2f",
                quantidade, produto.getNome(), getSubtotal());
    }
}
