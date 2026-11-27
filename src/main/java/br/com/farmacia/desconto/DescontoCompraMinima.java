package br.com.farmacia.desconto;

import br.com.farmacia.model.Cliente;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class DescontoCompraMinima implements PoliticaDesconto {

    private static final BigDecimal VALOR_MINIMO = new BigDecimal("100.00");
    private static final BigDecimal PERCENTUAL = new BigDecimal("0.10");

    @Override
    public BigDecimal calcularDesconto(BigDecimal subtotal, Cliente cliente) {
        if (seAplica(cliente) && subtotal.compareTo(VALOR_MINIMO) >= 0) {
            return subtotal.multiply(PERCENTUAL)
                    .setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean seAplica(Cliente cliente) {
        return cliente != null;
    }

    @Override
    public String getDescricao() {
        return "Desconto compra mínima (10%)";
    }
}