package br.com.farmacia.venda;

import br.com.farmacia.desconto.PoliticaDesconto;
import br.com.farmacia.desconto.SemDesconto;
import br.com.farmacia.model.Cliente;
import br.com.farmacia.model.ItemVenda;
import br.com.farmacia.model.Produto;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Venda — o produto final construído pelo Builder.
 *
 * Uma venda tem muitos campos, sendo vários opcionais (observação, entrega,
 * endereço, embrulho para presente) e combináveis de formas diferentes
 * (venda de balcão, venda com entrega, venda para presente). Para evitar
 * construtores telescópicos, a Venda é montada pelo PADRÃO BUILDER: o
 * construtor é privado e só o {@link Builder} cria instâncias.
 *
 * A venda também guarda uma {@link PoliticaDesconto} (PADRÃO STRATEGY) e
 * delega a ela o cálculo do desconto no total.
 */
public class Venda {

    // Obrigatórios
    private final Cliente cliente;
    private final List<ItemVenda> itens;
    private final PoliticaDesconto politicaDesconto;

    // Opcionais
    private final boolean entrega;
    private final String enderecoEntrega;
    private final boolean embrulhoPresente;
    private final String observacao;

    // Construtor PRIVADO: ninguém cria uma Venda diretamente, só pelo Builder.
    private Venda(Builder builder) {
        this.cliente = builder.cliente;
        this.itens = List.copyOf(builder.itens);
        this.politicaDesconto = builder.politicaDesconto;
        this.entrega = builder.entrega;
        this.enderecoEntrega = builder.enderecoEntrega;
        this.embrulhoPresente = builder.embrulhoPresente;
        this.observacao = builder.observacao;
    }

    /** Soma dos subtotais de todos os itens, antes do desconto. */
    public double getSubtotal() {
        double total = 0.0;
        for (ItemVenda item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    /** Valor do desconto, calculado pela estratégia (Strategy). */
    public double getDesconto() {
        return politicaDesconto.calcularDesconto(getSubtotal(), cliente);
    }

    /** Taxa fixa de entrega, somada apenas quando a venda tem entrega. */
    public double getTaxaEntrega() {
        return entrega ? 8.00 : 0.0;
    }

    /** Total final = subtotal - desconto + taxa de entrega. */
    public double getTotal() {
        return getSubtotal() - getDesconto() + getTaxaEntrega();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public PoliticaDesconto getPoliticaDesconto() {
        return politicaDesconto;
    }

    public boolean isEntrega() {
        return entrega;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public boolean isEmbrulhoPresente() {
        return embrulhoPresente;
    }

    public String getObservacao() {
        return observacao;
    }

    // ======================================================================
    //  PADRÃO BUILDER — classe interna estática.
    //
    //  Monta a Venda passo a passo com métodos fluentes (cada um devolve o
    //  próprio Builder, permitindo encadear). Os obrigatórios entram no
    //  construtor do Builder; os opcionais, em métodos próprios. O build()
    //  valida as regras e devolve a Venda pronta e imutável.
    // ======================================================================
    public static class Builder {

        // Obrigatórios
        private final Cliente cliente;
        private final List<ItemVenda> itens = new ArrayList<>();

        // Opcionais, com valores padrão
        private PoliticaDesconto politicaDesconto = new SemDesconto();
        private boolean entrega = false;
        private String enderecoEntrega = null;
        private boolean embrulhoPresente = false;
        private String observacao = null;

        /** O cliente é obrigatório para iniciar uma venda. */
        public Builder(Cliente cliente) {
            if (cliente == null) {
                throw new IllegalArgumentException("A venda precisa de um cliente.");
            }
            this.cliente = cliente;
        }

        /** Adiciona um item (produto + quantidade) à venda. */
        public Builder adicionarItem(Produto produto, int quantidade) {
            if (produto == null || quantidade <= 0) {
                throw new IllegalArgumentException("Item inválido na venda.");
            }
            this.itens.add(new ItemVenda(produto, quantidade));
            return this;
        }

        /** Define a política de desconto (Strategy) usada nesta venda. */
        public Builder comDesconto(PoliticaDesconto politica) {
            if (politica != null) {
                this.politicaDesconto = politica;
            }
            return this;
        }

        /** Marca a venda como entrega em domicílio, com o endereço. */
        public Builder comEntrega(String endereco) {
            this.entrega = true;
            this.enderecoEntrega = endereco;
            return this;
        }

        /** Marca que os itens vão embrulhados para presente. */
        public Builder comEmbrulhoPresente() {
            this.embrulhoPresente = true;
            return this;
        }

        /** Observação livre (ex.: "entregar após as 18h"). */
        public Builder comObservacao(String observacao) {
            this.observacao = observacao;
            return this;
        }

        /**
         * Valida as regras obrigatórias e devolve a Venda pronta.
         * É o único ponto em que uma Venda nasce — e só nasce se estiver
         * coerente.
         */
        public Venda build() {
            if (itens.isEmpty()) {
                throw new IllegalStateException("A venda precisa de pelo menos um item.");
            }
            if (entrega && (enderecoEntrega == null || enderecoEntrega.isBlank())) {
                throw new IllegalStateException("Venda com entrega exige um endereço.");
            }
            return new Venda(this);
        }
    }
}
