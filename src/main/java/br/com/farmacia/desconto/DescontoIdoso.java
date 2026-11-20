package br.com.farmacia.desconto;

import br.com.farmacia.model.Cliente;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ESTRATÉGIA CONCRETA — desconto para clientes idosos.
 *
 * Aplica 15% de desconto quando o cliente tem 60 anos ou mais; caso contrário,
 * não abate nada.
 */
public class DescontoIdoso implements PoliticaDesconto {

    private static final BigDecimal PERCENTUAL = new BigDecimal("0.15");

    @Override
    public BigDecimal calcularDesconto(BigDecimal subtotal, Cliente cliente) {
        if (seAplica(cliente)) {
            return subtotal.multiply(PERCENTUAL).setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
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
