package br.com.farmacia.desconto;

import br.com.farmacia.model.Cliente;

import java.math.BigDecimal;

/**
 * ESTRATÉGIA CONCRETA — nenhuma política de desconto.
 *
 * É a opção padrão: não abate nada. Serve também como "estratégia neutra",
 * evitando ter que checar null na Venda.
 */
public class SemDesconto implements PoliticaDesconto {

    @Override
    public BigDecimal calcularDesconto(BigDecimal subtotal, Cliente cliente) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean seAplica(Cliente cliente) {
        return true;
    }

    @Override
    public String getDescricao() {
        return "Sem desconto";
    }
}
