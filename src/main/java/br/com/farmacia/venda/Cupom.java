package br.com.farmacia.venda;

import br.com.farmacia.model.ItemVenda;
import br.com.farmacia.util.Moeda;

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
        sb.append("Subtotal.........: ").append(Moeda.formatar(venda.getSubtotal())).append("\n");
        if (venda.getDesconto().signum() > 0) {
            sb.append(venda.getPoliticaDesconto().getDescricao()).append(": -")
                    .append(Moeda.formatar(venda.getDesconto())).append("\n");
        }

        if (venda.isEntrega()) {
            sb.append("Taxa de entrega..: ").append(Moeda.formatar(venda.getTaxaEntrega())).append("\n");
            sb.append("Endereço.........: ").append(venda.getEnderecoEntrega()).append("\n");
        }
        if (venda.isEmbrulhoPresente()) {
            sb.append("Embrulho.........: presente\n");
        }
        if (venda.getReceita() != null && !venda.getReceita().isBlank()) {
            sb.append("Receita.........: ").append(venda.getReceita()).append("\n");
        }
        if (venda.getObservacao() != null && !venda.getObservacao().isBlank()) {
            sb.append("Observação.......: ").append(venda.getObservacao()).append("\n");
        }

        sb.append("---------------------------------------\n");
        sb.append("TOTAL............: ").append(Moeda.formatar(venda.getTotal())).append("\n");
        sb.append("=======================================\n");
        return sb.toString();
    }
}
