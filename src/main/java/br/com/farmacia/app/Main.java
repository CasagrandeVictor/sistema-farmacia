package br.com.farmacia.app;

import br.com.farmacia.desconto.DescontoFidelidade;
import br.com.farmacia.desconto.DescontoIdoso;
import br.com.farmacia.desconto.PoliticaDesconto;
import br.com.farmacia.desconto.SemDesconto;
import br.com.farmacia.model.Cliente;
import br.com.farmacia.model.Produto;
import br.com.farmacia.venda.Cupom;
import br.com.farmacia.venda.Venda;

import java.math.BigDecimal;
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
            new Produto("Dipirona 500mg", new BigDecimal("12.90"), false),
            new Produto("Protetor Solar FPS 50", new BigDecimal("59.90"), false),
            new Produto("Amoxicilina 500mg", new BigDecimal("34.50"), true),
            new Produto("Vitamina C", new BigDecimal("25.00"), false),
            new Produto("Álcool em gel 70%", new BigDecimal("9.90"), false)
    );

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Sistema de Farmácia (Strategy + Builder) ===");

        try {
            boolean rodando = true;
            while (rodando) {
                System.out.println();
                System.out.println("1 - Rodar demonstração");
                System.out.println("2 - Montar uma venda");
                System.out.println("0 - Sair");
                String opcao = lerLinha(sc, "Escolha: ");

                switch (opcao) {
                    case "1" -> demonstracao();
                    case "2" -> montarVenda(sc);
                    case "0" -> rodando = false;
                    default -> System.out.println("Opção inválida.");
                }
            }
        } catch (FimDaEntrada e) {
            // A entrada acabou (ex.: Ctrl+D ou fim do pipe): encerra normalmente.
            System.out.println();
        }
        System.out.println("Até logo!");
    }

    /**
     * Demonstração automática com 4 vendas: a MESMA cesta de produtos vendida
     * com três políticas de desconto diferentes (Strategy trocando em runtime),
     * usando o Builder para montar formatos de venda diferentes (balcão,
     * entrega e presente), e uma venda de medicamento controlado (Amoxicilina)
     * que o Builder só aceita com a identificação da receita médica.
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

        // Venda 4: medicamento controlado -> o Builder só aceita com receita
        Venda v4 = new Venda.Builder(ana)
                .adicionarItem(CATALOGO.get(2), 1) // Amoxicilina (exige receita)
                .comReceita("CRM-SC 12345")
                .build();
        System.out.println(Cupom.formatar(v4));
    }

    /** Fluxo interativo: o usuário monta a própria venda. */
    private static void montarVenda(Scanner sc) {
        System.out.println("\n----- NOVA VENDA -----");

        Cliente cliente = lerCliente(sc);

        // Começa a montar a venda com o Builder
        Venda.Builder builder = new Venda.Builder(cliente);

        // Escolha dos produtos
        String receita = null; // pedida só quando entra um produto controlado
        int itensAdicionados = 0;
        boolean adicionando = true;
        while (adicionando) {
            System.out.println("\nCatálogo:");
            for (int i = 0; i < CATALOGO.size(); i++) {
                System.out.printf("  %d - %s%n", i + 1, CATALOGO.get(i));
            }
            int escolha = lerInteiro(sc, "Produto (número, 0 para encerrar): ");
            if (escolha == 0) {
                if (itensAdicionados == 0) {
                    System.out.println("Adicione ao menos um item.");
                } else {
                    adicionando = false;
                }
            } else if (escolha >= 1 && escolha <= CATALOGO.size()) {
                Produto produto = CATALOGO.get(escolha - 1);
                if (produto.isExigeReceita() && receita == null) {
                    if (!lerSimNao(sc, "Cliente apresentou receita? (s/n): ")) {
                        System.out.println("Item recusado: " + produto.getNome()
                                + " só pode ser vendido com receita médica.");
                        continue;
                    }
                    receita = lerTextoObrigatorio(sc, "Identificação da receita (CRM/número): ");
                    builder.comReceita(receita);
                }
                int qtd = lerInteiro(sc, "Quantidade: ");
                try {
                    builder.adicionarItem(produto, qtd);
                    itensAdicionados++;
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
        PoliticaDesconto politica = null;
        while (politica == null) {
            String op = lerLinha(sc, "Escolha: ");
            politica = switch (op) {
                case "1" -> new SemDesconto();
                case "2" -> new DescontoIdoso();
                case "3" -> new DescontoFidelidade();
                default -> null;
            };
            if (politica == null) {
                System.out.println("Opção inválida.");
            }
        }
        if (!politica.seAplica(cliente)) {
            System.out.println("Cliente não elegível para " + politica.getDescricao()
                    + "; venda seguirá sem desconto.");
            politica = new SemDesconto();
        }
        builder.comDesconto(politica);

        // Entrega?
        if (lerSimNao(sc, "Venda com entrega? (s/n): ")) {
            builder.comEntrega(lerTextoObrigatorio(sc, "Endereço de entrega: "));
        }

        // Embrulho para presente?
        if (lerSimNao(sc, "Embrulhar para presente? (s/n): ")) {
            builder.comEmbrulhoPresente();
        }

        // Observação opcional
        String observacao = lerLinha(sc, "Observação (Enter para pular): ");
        if (!observacao.isBlank()) {
            builder.comObservacao(observacao);
        }

        // Monta e imprime
        try {
            Venda venda = builder.build();
            System.out.println("\n" + Cupom.formatar(venda));
        } catch (RuntimeException e) {
            System.out.println("Não foi possível fechar a venda: " + e.getMessage());
        }
    }

    /** Pede nome, idade e fidelidade, repetindo até nome e idade serem válidos. */
    private static Cliente lerCliente(Scanner sc) {
        String nome = lerTextoObrigatorio(sc, "Nome do cliente: ");
        int idade = lerInteiro(sc, "Idade do cliente: ");
        while (idade < 0 || idade > Cliente.IDADE_MAXIMA) {
            System.out.println("Idade deve estar entre 0 e " + Cliente.IDADE_MAXIMA + " anos.");
            idade = lerInteiro(sc, "Idade do cliente: ");
        }
        boolean fidelidade = lerSimNao(sc, "Cliente fidelidade? (s/n): ");
        return new Cliente(nome, idade, fidelidade);
    }

    /**
     * Lê uma linha da entrada, sem espaços nas pontas. Se a entrada acabar,
     * lança {@link FimDaEntrada} para o programa encerrar sem erro.
     */
    private static String lerLinha(Scanner sc, String prompt) {
        System.out.print(prompt);
        if (!sc.hasNextLine()) {
            throw new FimDaEntrada();
        }
        return sc.nextLine().trim();
    }

    /** Repete a pergunta até o usuário digitar algo que não esteja em branco. */
    private static String lerTextoObrigatorio(Scanner sc, String prompt) {
        while (true) {
            String linha = lerLinha(sc, prompt);
            if (!linha.isBlank()) {
                return linha;
            }
            System.out.println("Este campo não pode ficar em branco.");
        }
    }

    private static boolean lerSimNao(Scanner sc, String prompt) {
        return lerLinha(sc, prompt).equalsIgnoreCase("s");
    }

    private static int lerInteiro(Scanner sc, String prompt) {
        while (true) {
            String linha = lerLinha(sc, prompt);
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    /** Sinaliza que a entrada padrão terminou. */
    private static final class FimDaEntrada extends RuntimeException {
        private static final long serialVersionUID = 1L;

        FimDaEntrada() {
            super(null, null, false, false);
        }
    }
}
