package br.com.farmacia.desconto;

import br.com.farmacia.model.Cliente;

/**
 * ESTRATÉGIA CONCRETA — nenhuma política de desconto.
 *
 * É a opção padrão: não abate nada. Serve também como "estratégia neutra",
 * evitando ter que checar null na Venda.
 */
public class SemDesconto implements PoliticaDesconto {

    @Override
    public double calcularDesconto(double subtotal, Cliente cliente) {
        return 0.0;
    }

    @Override
    public String getDescricao() {
        return "Sem desconto";
    }
}
