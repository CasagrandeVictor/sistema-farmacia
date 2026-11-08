package br.com.farmacia;

import br.com.farmacia.desconto.DescontoFidelidade;
import br.com.farmacia.desconto.DescontoIdoso;
import br.com.farmacia.desconto.SemDesconto;
import br.com.farmacia.model.Cliente;
import br.com.farmacia.model.Produto;
import br.com.farmacia.venda.Venda;

/**
 * Teste manual simples (sem framework, para não precisar de dependências).
 * Verifica as regras principais dos dois padrões. Roda com:
 *
 *   javac -d target/test-classes (fontes) e depois
 *   java -cp target/classes:target/test-classes br.com.farmacia.TesteManual
 *
 * Imprime OK/FALHA para cada verificação e sai com código 1 se algo falhar.
 */
public class TesteManual {

    private static int falhas = 0;

    public static void main(String[] args) {
        Produto dipirona = new Produto("Dipirona", 10.00, false);
        Produto vitamina = new Produto("Vitamina C", 20.00, false);

        Cliente idoso = new Cliente("João", 70, false);
        Cliente fiel = new Cliente("Maria", 30, true);
        Cliente comum = new Cliente("Ana", 25, false);

        // STRATEGY: desconto idoso = 15% sobre 40,00 = 6,00 -> total 34,00
        Venda v1 = new Venda.Builder(idoso)
                .adicionarItem(dipirona, 2)   // 20,00
                .adicionarItem(vitamina, 1)   // 20,00
                .comDesconto(new DescontoIdoso())
                .build();
        verifica("desconto idoso (15%)", 6.00, v1.getDesconto());
        verifica("total com desconto idoso", 34.00, v1.getTotal());

        // STRATEGY: fidelidade = 10% sobre 40,00 = 4,00
        Venda v2 = new Venda.Builder(fiel)
                .adicionarItem(dipirona, 2)
                .adicionarItem(vitamina, 1)
                .comDesconto(new DescontoFidelidade())
                .build();
        verifica("desconto fidelidade (10%)", 4.00, v2.getDesconto());

        // STRATEGY: sem desconto = 0
        Venda v3 = new Venda.Builder(comum)
                .adicionarItem(vitamina, 1)
                .comDesconto(new SemDesconto())
                .build();
        verifica("sem desconto", 0.00, v3.getDesconto());

        // BUILDER: venda com entrega soma a taxa de 8,00
        Venda v4 = new Venda.Builder(comum)
                .adicionarItem(vitamina, 1)   // 20,00
                .comEntrega("Rua X, 10")
                .build();
        verifica("taxa de entrega", 8.00, v4.getTaxaEntrega());
        verifica("total com entrega", 28.00, v4.getTotal());

        // BUILDER: venda sem itens deve falhar no build()
        try {
            new Venda.Builder(comum).build();
            System.out.println("FALHA: venda sem itens deveria lançar exceção");
            falhas++;
        } catch (IllegalStateException e) {
            System.out.println("OK: venda sem itens é rejeitada");
        }

        // BUILDER: entrega sem endereço deve falhar
        try {
            new Venda.Builder(comum).adicionarItem(vitamina, 1).comEntrega("  ").build();
            System.out.println("FALHA: entrega sem endereço deveria lançar exceção");
            falhas++;
        } catch (IllegalStateException e) {
            System.out.println("OK: entrega sem endereço é rejeitada");
        }

        System.out.println("\n" + (falhas == 0 ? "TODOS OS TESTES PASSARAM" : falhas + " TESTE(S) FALHARAM"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void verifica(String nome, double esperado, double obtido) {
        if (Math.abs(esperado - obtido) < 0.001) {
            System.out.printf("OK: %s = %.2f%n", nome, obtido);
        } else {
            System.out.printf("FALHA: %s esperado %.2f, obtido %.2f%n", nome, esperado, obtido);
            falhas++;
        }
    }
}
