package br.com.farmacia.desconto;

import br.com.farmacia.model.Cliente;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ESTRATÉGIA CONCRETA — desconto para clientes com carteira do INSS.
 *
 * Aplica 7% de desconto quando o cliente tem carteira INSS.
 */
public class DescontoCarteiraINSS implements PoliticaDesconto {

    private static final BigDecimal PERCENTUAL = new BigDecimal("0.07");

    @Override
    public BigDecimal calcularDesconto(BigDecimal subtotal, Cliente cliente) {
        if (seAplica(cliente)) {
            return subtotal.multiply(PERCENTUAL).setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean seAplica(Cliente cliente) {
        return cliente != null && cliente.hasCarteiraINSS();
    }

    @Override
    public String getDescricao() {
        return "Desconto fidelidade (7%)";
    }
}
