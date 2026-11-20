package br.com.farmacia.desconto;

import br.com.farmacia.model.Cliente;

/**
 * ESTRATÉGIA CONCRETA — desconto para clientes idosos.
 *
 * Aplica 15% de desconto quando o cliente tem 60 anos ou mais; caso contrário,
 * não abate nada.
 */
public class DescontoIdoso implements PoliticaDesconto {

    private static final double PERCENTUAL = 0.15;

    @Override
    public double calcularDesconto(double subtotal, Cliente cliente) {
        if (seAplica(cliente)) {
            return subtotal * PERCENTUAL;
        }
        return 0.0;
    }

    @Override
    public boolean seAplica(Cliente cliente) {
        return cliente != null && cliente.isIdoso();
    }

    @Override
    public String getDescricao() {
        return "Desconto idoso (15%)";
    }
}
