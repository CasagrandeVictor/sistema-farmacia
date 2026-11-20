package br.com.farmacia;

import br.com.farmacia.desconto.DescontoFidelidade;
import br.com.farmacia.desconto.DescontoIdoso;
import br.com.farmacia.desconto.SemDesconto;
import br.com.farmacia.model.Cliente;
import br.com.farmacia.model.ItemVenda;
import br.com.farmacia.model.Produto;
import br.com.farmacia.venda.Cupom;
import br.com.farmacia.venda.Venda;

import java.math.BigDecimal;
import java.util.Objects;

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
        Produto dipirona = new Produto("Dipirona", new BigDecimal("10.00"), false);
        Produto vitamina = new Produto("Vitamina C", new BigDecimal("20.00"), false);

        Cliente idoso = new Cliente("João", 70, false);
        Cliente fiel = new Cliente("Maria", 30, true);
        Cliente comum = new Cliente("Ana", 25, false);

        // STRATEGY: desconto idoso = 15% sobre 40,00 = 6,00 -> total 34,00
        Venda v1 = new Venda.Builder(idoso)
                .adicionarItem(dipirona, 2)   // 20,00
                .adicionarItem(vitamina, 1)   // 20,00
                .comDesconto(new DescontoIdoso())
                .build();
        verifica("desconto idoso (15%)", "6.00", v1.getDesconto());
        verifica("total com desconto idoso", "34.00", v1.getTotal());

        // STRATEGY: fidelidade = 10% sobre 40,00 = 4,00
        Venda v2 = new Venda.Builder(fiel)
                .adicionarItem(dipirona, 2)
                .adicionarItem(vitamina, 1)
                .comDesconto(new DescontoFidelidade())
                .build();
        verifica("desconto fidelidade (10%)", "4.00", v2.getDesconto());

        // STRATEGY: sem desconto = 0
        Venda v3 = new Venda.Builder(comum)
                .adicionarItem(vitamina, 1)
                .comDesconto(new SemDesconto())
                .build();
        verifica("sem desconto", "0.00", v3.getDesconto());

        // BUILDER: venda com entrega soma a taxa de 8,00
        Venda v4 = new Venda.Builder(comum)
                .adicionarItem(vitamina, 1)   // 20,00
                .comEntrega("Rua X, 10")
                .build();
        verifica("taxa de entrega", "8.00", v4.getTaxaEntrega());
        verifica("total com entrega", "28.00", v4.getTotal());

        // STRATEGY: a política escolhida só aplica desconto a clientes elegíveis.
        verifica("cliente comum sem desconto idoso", "0.00",
                new DescontoIdoso().calcularDesconto(new BigDecimal("40.00"), comum));
        verifica("cliente comum sem desconto fidelidade", "0.00",
                new DescontoFidelidade().calcularDesconto(new BigDecimal("40.00"), comum));
        verifica("cliente de 59 anos sem desconto idoso", "0.00",
                new DescontoIdoso().calcularDesconto(new BigDecimal("40.00"), new Cliente("Pedro", 59, false)));
        verifica("cliente de 60 anos com desconto idoso", "6.00",
                new DescontoIdoso().calcularDesconto(new BigDecimal("40.00"), new Cliente("Paula", 60, false)));

        // STRATEGY: seAplica indica a elegibilidade do cliente para cada política.
        verificaVerdadeiro("sem desconto se aplica a qualquer cliente",
                new SemDesconto().seAplica(comum));
        verificaVerdadeiro("desconto idoso se aplica a idoso", new DescontoIdoso().seAplica(idoso));
        verificaVerdadeiro("desconto idoso não se aplica a cliente comum",
                !new DescontoIdoso().seAplica(comum));
        verificaVerdadeiro("desconto idoso se aplica a 60 anos",
                new DescontoIdoso().seAplica(new Cliente("Paula", 60, false)));
        verificaVerdadeiro("desconto fidelidade se aplica a cliente fidelidade",
                new DescontoFidelidade().seAplica(fiel));
        verificaVerdadeiro("desconto fidelidade não se aplica a idoso sem cadastro",
                !new DescontoFidelidade().seAplica(idoso));

        // BUILDER: reutilizar o construtor não pode alterar uma venda já finalizada.
        Venda.Builder builder = new Venda.Builder(fiel)
                .adicionarItem(vitamina, 1)
                .comDesconto(new DescontoFidelidade());
        Venda primeiraVenda = builder.build();
        Venda segundaVenda = builder.adicionarItem(dipirona, 2).build();
        builder.adicionarItem(vitamina, 1);

        verificaQuantidade("primeira venda mantém um item", 1, primeiraVenda.getItens().size());
        verifica("primeira venda mantém subtotal", "20.00", primeiraVenda.getSubtotal());
        verifica("primeira venda mantém desconto", "2.00", primeiraVenda.getDesconto());
        verifica("primeira venda mantém total", "18.00", primeiraVenda.getTotal());
        verificaQuantidade("segunda venda mantém dois itens", 2, segundaVenda.getItens().size());
        verifica("segunda venda mantém subtotal", "40.00", segundaVenda.getSubtotal());
        verifica("segunda venda mantém desconto", "4.00", segundaVenda.getDesconto());
        verifica("segunda venda mantém total", "36.00", segundaVenda.getTotal());
        verificaExcecao("itens da venda não podem ser removidos externamente",
                UnsupportedOperationException.class, () -> primeiraVenda.getItens().clear());
        verificaExcecao("itens da venda não podem ser adicionados externamente",
                UnsupportedOperationException.class,
                () -> primeiraVenda.getItens().add(segundaVenda.getItens().get(0)));

        // BUILDER: valida os dados obrigatórios antes de montar a venda.
        verificaExcecao("cliente nulo é rejeitado", IllegalArgumentException.class,
                () -> new Venda.Builder(null));
        verificaExcecao("produto nulo é rejeitado", IllegalArgumentException.class,
                () -> new Venda.Builder(comum).adicionarItem(null, 1));
        verificaExcecao("quantidade zero é rejeitada", IllegalArgumentException.class,
                () -> new Venda.Builder(comum).adicionarItem(vitamina, 0));
        verificaExcecao("quantidade negativa é rejeitada", IllegalArgumentException.class,
                () -> new Venda.Builder(comum).adicionarItem(vitamina, -1));

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

        // CUPOM: valores em formato brasileiro, arredondamento e linha de desconto
        Produto dipirona500 = new Produto("Dipirona 500mg", new BigDecimal("12.90"), false);
        Produto vitaminaC = new Produto("Vitamina C", new BigDecimal("25.00"), false);
        Venda demo = new Venda.Builder(idoso)
                .adicionarItem(dipirona500, 2)
                .adicionarItem(vitaminaC, 1)
                .comDesconto(new DescontoIdoso())
                .build();
        verifica("desconto idoso sobre 50,80", "7.62", demo.getDesconto());
        verifica("total com desconto idoso sobre 50,80", "43.18", demo.getTotal());
        String cupom = Cupom.formatar(demo);
        verificaVerdadeiro("cupom usa vírgula decimal", cupom.contains("TOTAL............: R$ 43,18"));
        verificaVerdadeiro("cupom mostra o desconto aplicado",
                cupom.contains("Desconto idoso (15%): -R$ 7,62"));
        verificaVerdadeiro("cupom omite desconto zerado",
                !Cupom.formatar(v3).contains("Sem desconto"));
        verifica("desconto arredonda com HALF_EVEN", "0.12", new DescontoFidelidade()
                .calcularDesconto(new BigDecimal("1.25"), fiel)); // 0,125 -> 0,12

        // ENTIDADES: validação dos dados de entrada
        verificaExcecao("cliente com nome nulo é rejeitado", IllegalArgumentException.class,
                () -> new Cliente(null, 30, false));
        verificaExcecao("cliente com nome em branco é rejeitado", IllegalArgumentException.class,
                () -> new Cliente("  ", 30, false));
        verificaExcecao("cliente com idade negativa é rejeitado", IllegalArgumentException.class,
                () -> new Cliente("Ana", -1, false));
        verificaExcecao("cliente com idade acima de 130 é rejeitado",
                IllegalArgumentException.class, () -> new Cliente("Ana", 131, false));
        verificaVerdadeiro("cliente com idade 0 e 130 é aceito",
                new Cliente("Bebê", 0, false).getIdade() == 0
                        && new Cliente("Ana", 130, false).getIdade() == 130);
        verificaExcecao("produto com nome em branco é rejeitado", IllegalArgumentException.class,
                () -> new Produto(" ", new BigDecimal("10.00"), false));
        verificaExcecao("produto com preço zero é rejeitado", IllegalArgumentException.class,
                () -> new Produto("Dipirona", new BigDecimal("0.00"), false));
        verificaExcecao("produto com preço negativo é rejeitado", IllegalArgumentException.class,
                () -> new Produto("Dipirona", new BigDecimal("-1.00"), false));
        verificaExcecao("item com produto nulo é rejeitado", IllegalArgumentException.class,
                () -> new ItemVenda(null, 1));
        verificaExcecao("item com quantidade zero é rejeitado", IllegalArgumentException.class,
                () -> new ItemVenda(vitamina, 0));

        // BUILDER: produto controlado exige receita médica
        Produto amoxicilina = new Produto("Amoxicilina", new BigDecimal("30.00"), true);
        verificaExcecao("produto controlado sem receita é rejeitado", IllegalStateException.class,
                () -> new Venda.Builder(comum).adicionarItem(amoxicilina, 1).build());
        verificaExcecao("produto controlado com receita em branco é rejeitado",
                IllegalStateException.class,
                () -> new Venda.Builder(comum).adicionarItem(amoxicilina, 1).comReceita(" ").build());
        Venda comReceita = new Venda.Builder(comum)
                .adicionarItem(amoxicilina, 1)
                .comReceita("CRM-SC 12345")
                .build();
        verificaTexto("produto controlado com receita é aceito", "CRM-SC 12345",
                comReceita.getReceita());
        Venda semControlado = new Venda.Builder(comum).adicionarItem(vitamina, 1).build();
        verificaTexto("venda sem produto controlado não exige receita", null,
                semControlado.getReceita());

        System.out.println("\n" + (falhas == 0 ? "TODOS OS TESTES PASSARAM" : falhas + " TESTE(S) FALHARAM"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    /** Compara valores monetários com compareTo (ignora a escala: 6 == 6.00). */
    private static void verifica(String nome, String esperado, BigDecimal obtido) {
        if (obtido != null && new BigDecimal(esperado).compareTo(obtido) == 0) {
            System.out.printf("OK: %s = %s%n", nome, obtido);
        } else {
            System.out.printf("FALHA: %s esperado %s, obtido %s%n", nome, esperado, obtido);
            falhas++;
        }
    }

    private static void verificaQuantidade(String nome, int esperado, int obtido) {
        if (esperado == obtido) {
            System.out.printf("OK: %s = %d%n", nome, obtido);
        } else {
            System.out.printf("FALHA: %s esperado %d, obtido %d%n", nome, esperado, obtido);
            falhas++;
        }
    }

    private static void verificaVerdadeiro(String nome, boolean condicao) {
        if (condicao) {
            System.out.println("OK: " + nome);
        } else {
            System.out.println("FALHA: " + nome);
            falhas++;
        }
    }

    private static void verificaTexto(String nome, String esperado, String obtido) {
        if (Objects.equals(esperado, obtido)) {
            System.out.println("OK: " + nome);
        } else {
            System.out.printf("FALHA: %s esperado %s, obtido %s%n", nome, esperado, obtido);
            falhas++;
        }
    }

    private static void verificaExcecao(String nome, Class<? extends RuntimeException> tipo,
                                      Runnable acao) {
        try {
            acao.run();
            System.out.println("FALHA: " + nome + " deveria lançar " + tipo.getSimpleName());
            falhas++;
        } catch (RuntimeException e) {
            if (tipo.isInstance(e)) {
                System.out.println("OK: " + nome);
            } else {
                System.out.println("FALHA: " + nome + " lançou " + e.getClass().getSimpleName());
                falhas++;
            }
        }
    }
}
