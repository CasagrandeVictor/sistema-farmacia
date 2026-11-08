package br.com.farmacia.app;

import br.com.farmacia.desconto.DescontoFidelidade;
import br.com.farmacia.desconto.DescontoIdoso;
import br.com.farmacia.desconto.PoliticaDesconto;
import br.com.farmacia.desconto.SemDesconto;
import br.com.farmacia.model.Cliente;
import br.com.farmacia.model.Produto;
import br.com.farmacia.venda.Cupom;
import br.com.farmacia.venda.Venda;

import java.util.List;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação de farmácia.
 *
 * Oferece um menu de console: o usuário pode rodar uma demonstração pronta
 * (que mostra os dois padrões em ação) ou montar a própria venda passo a passo.
 * O fluxo de ponta a ponta é: escolher o cliente, escolher os produtos,
 * escolher a política de desconto (Strategy), montar a venda (Builder) e
 * imprimir o cupom.
 */
public class Main {

    // Catálogo fixo em memória (sem banco de dados).
    private static final List<Produto> CATALOGO = List.of(
            new Produto("Dipirona 500mg", 12.90, false),
            new Produto("Protetor Solar FPS 50", 59.90, false),
            new Produto("Amoxicilina 500mg", 34.50, true),
            new Produto("Vitamina C", 25.00, false),
            new Produto("Álcool em gel 70%", 9.90, false)
    );

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Sistema de Farmácia (Strategy + Builder) ===");

        boolean rodando = true;
        while (rodando) {
            System.out.println();
            System.out.println("1 - Rodar demonstração");
            System.out.println("2 - Montar uma venda");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1" -> demonstracao();
                case "2" -> montarVenda(sc);
                case "0" -> rodando = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Até logo!");
    }

    /**
     * Demonstração automática: a MESMA cesta de produtos vendida com três
     * políticas de desconto diferentes (Strategy trocando em runtime) e usando
     * o Builder para montar formatos de venda diferentes (balcão x entrega).
     */
    private static void demonstracao() {
        System.out.println("\n----- DEMONSTRAÇÃO -----");

        Cliente joao = new Cliente("João", 70, false);      // idoso
        Cliente maria = new Cliente("Maria", 30, true);     // fidelidade
        Cliente ana = new Cliente("Ana", 25, false);        // comum

        Produto p1 = CATALOGO.get(0); // Dipirona
        Produto p2 = CATALOGO.get(3); // Vitamina C

        // Venda 1: balcão, cliente idoso -> estratégia DescontoIdoso
        Venda v1 = new Venda.Builder(joao)
                .adicionarItem(p1, 2)
                .adicionarItem(p2, 1)
                .comDesconto(new DescontoIdoso())
                .comObservacao("Venda de balcão")
                .build();
        System.out.println(Cupom.formatar(v1));

        // Venda 2: MESMOS itens, cliente fidelidade -> estratégia DescontoFidelidade,
        // agora com ENTREGA (Builder monta outro formato de venda)
        Venda v2 = new Venda.Builder(maria)
                .adicionarItem(p1, 2)
                .adicionarItem(p2, 1)
                .comDesconto(new DescontoFidelidade())
                .comEntrega("Rua das Flores, 123")
                .build();
        System.out.println(Cupom.formatar(v2));

        // Venda 3: cliente comum -> SemDesconto, embrulho para presente
        Venda v3 = new Venda.Builder(ana)
                .adicionarItem(p2, 3)
                .comDesconto(new SemDesconto())
                .comEmbrulhoPresente()
                .comObservacao("Presente de aniversário")
                .build();
        System.out.println(Cupom.formatar(v3));
    }

    /** Fluxo interativo: o usuário monta a própria venda. */
    private static void montarVenda(Scanner sc) {
        System.out.println("\n----- NOVA VENDA -----");

        System.out.print("Nome do cliente: ");
        String nome = sc.nextLine().trim();
        int idade = lerInteiro(sc, "Idade do cliente: ");
        System.out.print("Cliente fidelidade? (s/n): ");
        boolean fidelidade = sc.nextLine().trim().equalsIgnoreCase("s");
        Cliente cliente = new Cliente(nome, idade, fidelidade);

        // Começa a montar a venda com o Builder
        Venda.Builder builder = new Venda.Builder(cliente);

        // Escolha dos produtos
        boolean adicionando = true;
        while (adicionando) {
            System.out.println("\nCatálogo:");
            for (int i = 0; i < CATALOGO.size(); i++) {
                System.out.printf("  %d - %s%n", i + 1, CATALOGO.get(i));
            }
            int escolha = lerInteiro(sc, "Produto (número, 0 para encerrar): ");
            if (escolha == 0) {
                adicionando = false;
            } else if (escolha >= 1 && escolha <= CATALOGO.size()) {
                int qtd = lerInteiro(sc, "Quantidade: ");
                try {
                    builder.adicionarItem(CATALOGO.get(escolha - 1), qtd);
                    System.out.println("Item adicionado.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            } else {
                System.out.println("Produto inválido.");
            }
        }

        // Escolha da política de desconto (Strategy)
        System.out.println("\nPolítica de desconto:");
        System.out.println("  1 - Sem desconto");
        System.out.println("  2 - Desconto idoso (15%)");
        System.out.println("  3 - Desconto fidelidade (10%)");
        String op = sc.nextLine().trim();
        PoliticaDesconto politica = switch (op) {
            case "2" -> new DescontoIdoso();
            case "3" -> new DescontoFidelidade();
            default -> new SemDesconto();
        };
        builder.comDesconto(politica);

        // Entrega?
        System.out.print("Venda com entrega? (s/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.print("Endereço de entrega: ");
            builder.comEntrega(sc.nextLine().trim());
        }

        // Monta e imprime
        try {
            Venda venda = builder.build();
            System.out.println("\n" + Cupom.formatar(venda));
        } catch (RuntimeException e) {
            System.out.println("Não foi possível fechar a venda: " + e.getMessage());
        }
    }

    private static int lerInteiro(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = sc.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }
}
