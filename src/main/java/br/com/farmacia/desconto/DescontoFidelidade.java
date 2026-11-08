package br.com.farmacia.desconto;

import br.com.farmacia.model.Cliente;

/**
 * ESTRATÉGIA CONCRETA — desconto para clientes do programa de fidelidade.
 *
 * Aplica 10% de desconto quando o cliente tem cadastro de fidelidade.
 */
public class DescontoFidelidade implements PoliticaDesconto {

    private static final double PERCENTUAL = 0.10;

    @Override
    public double calcularDesconto(double subtotal, Cliente cliente) {
        if (cliente != null && cliente.isFidelidade()) {
            return subtotal * PERCENTUAL;
        }
        return 0.0;
    }

    @Override
    public String getDescricao() {
        return "Desconto fidelidade (10%)";
    }
}
