package br.com.farmacia.venda;

import br.com.farmacia.model.ItemVenda;

/**
 * Formata uma {@link Venda} como um cupom de texto legível no console.
 * Mantém a lógica de exibição separada da entidade Venda.
 */
public final class Cupom {

    private Cupom() {
    }

    public static String formatar(Venda venda) {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================\n");
        sb.append("         FARMÁCIA SAÚDE & BEM          \n");
        sb.append("=======================================\n");
        sb.append("Cliente: ").append(venda.getCliente()).append("\n");
        sb.append("---------------------------------------\n");

        for (ItemVenda item : venda.getItens()) {
            sb.append(item).append("\n");
        }

        sb.append("---------------------------------------\n");
        sb.append(String.format("Subtotal.........: R$ %.2f%n", venda.getSubtotal()));
        sb.append(String.format("%s: -R$ %.2f%n",
                venda.getPoliticaDesconto().getDescricao(), venda.getDesconto()));

        if (venda.isEntrega()) {
            sb.append(String.format("Taxa de entrega..: R$ %.2f%n", venda.getTaxaEntrega()));
            sb.append("Endereço.........: ").append(venda.getEnderecoEntrega()).append("\n");
        }
        if (venda.isEmbrulhoPresente()) {
            sb.append("Embrulho.........: presente\n");
        }
        if (venda.getObservacao() != null && !venda.getObservacao().isBlank()) {
            sb.append("Observação.......: ").append(venda.getObservacao()).append("\n");
        }

        sb.append("---------------------------------------\n");
        sb.append(String.format("TOTAL............: R$ %.2f%n", venda.getTotal()));
        sb.append("=======================================\n");
        return sb.toString();
    }
}
