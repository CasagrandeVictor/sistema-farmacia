package br.com.farmacia.desconto;

import br.com.farmacia.model.Cliente;

/**
 * PADRÃO STRATEGY — interface da estratégia.
 *
 * Define o contrato de uma política de desconto. Cada forma de calcular
 * desconto (idoso, fidelidade, nenhum...) é uma classe concreta que implementa
 * esta interface. A Venda guarda uma PoliticaDesconto e apenas delega o
 * cálculo, sem saber qual regra está sendo usada — e por isso a regra pode ser
 * trocada em tempo de execução.
 */
public interface PoliticaDesconto {

    /**
     * Calcula o valor do desconto (em reais) sobre o subtotal informado.
     *
     * @param subtotal valor da venda antes do desconto
     * @param cliente  cliente da venda (algumas políticas dependem dele)
     * @return o valor a ser abatido do subtotal
     */
    double calcularDesconto(double subtotal, Cliente cliente);

    /** Nome da política, usado na exibição do cupom. */
    String getDescricao();
}
