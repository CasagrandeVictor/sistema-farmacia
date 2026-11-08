# Sistema de Farmácia — Strategy + Builder

Aplicação Java de console que simula o fluxo de vendas de uma farmácia.
O sistema cadastra produtos e clientes, monta uma venda e emite o cupom com o
total calculado. Dois padrões de projeto estruturam o código: **Strategy**
(políticas de desconto) e **Builder** (montagem da venda).

> Projeto Final de Design Patterns — Parte 1 · Tema: **Farmácia** · Padrões: **Strategy + Builder**

---

## Descrição do sistema

A farmácia vende produtos a clientes. Cada venda é composta por um cliente, uma
lista de itens (produto + quantidade) e uma política de desconto. Dependendo do
cliente, aplica-se um desconto diferente (idoso, fidelidade ou nenhum), e a
venda pode ter formatos diferentes (balcão, com entrega, com embrulho para
presente). No fim, o sistema imprime um cupom com subtotal, desconto, eventual
taxa de entrega e total.

### Entidades principais

- **Produto** — nome, preço e se exige receita.
- **Cliente** — nome, idade e se participa do programa de fidelidade.
- **Venda** — cliente, itens, política de desconto e opções de formato; é o
  objeto montado pelo Builder.

### Fluxo de ponta a ponta

1. O usuário escolhe um cliente (nome, idade, fidelidade).
2. Seleciona produtos do catálogo e as quantidades.
3. Escolhe a política de desconto (Strategy).
4. Define o formato da venda — balcão, entrega ou presente (Builder).
5. O sistema valida, monta a venda e imprime o cupom com o total.

---

## Tecnologias e pré-requisitos

- **Java 17 ou superior** (desenvolvido e testado com Java 21)
- **Maven 3.8+** (para compilar e executar)
- Não usa banco de dados nem dependências externas — os dados ficam em memória.

---

## Como executar

Na raiz do projeto (onde está o `pom.xml`):

```bash
# Compilar
mvn compile

# Executar a aplicação (menu interativo)
mvn exec:java
```

Alternativa gerando o JAR:

```bash
mvn package
java -jar target/sistema-farmacia.jar
```

Sem Maven, direto com o JDK:

```bash
javac -encoding UTF-8 -d target/classes $(find src/main -name "*.java")
java -cp target/classes br.com.farmacia.app.Main
```

Ao iniciar, escolha **1** para ver a demonstração automática (que exibe os dois
padrões em ação) ou **2** para montar a sua própria venda.

### Rodando os testes

```bash
javac -encoding UTF-8 -d target/classes $(find src/main -name "*.java")
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/br/com/farmacia/TesteManual.java
java -cp target/classes:target/test-classes br.com.farmacia.TesteManual
```

(no Windows, troque `:` por `;` no classpath)

---

## Exemplos de uso

Demonstração (opção 1) — a mesma cesta vendida com descontos e formatos diferentes:

```
=======================================
         FARMÁCIA SAÚDE & BEM
=======================================
Cliente: João (70 anos)
---------------------------------------
2x Dipirona 500mg = R$ 25.80
1x Vitamina C = R$ 25.00
---------------------------------------
Subtotal.........: R$ 50.80
Desconto idoso (15%): -R$ 7.62
Observação.......: Venda de balcão
---------------------------------------
TOTAL............: R$ 43.18
=======================================
```

A mesma cesta para uma cliente fidelidade, agora com entrega:

```
Cliente: Maria (30 anos) - cliente fidelidade
Subtotal.........: R$ 50.80
Desconto fidelidade (10%): -R$ 5.08
Taxa de entrega..: R$ 8.00
Endereço.........: Rua das Flores, 123
TOTAL............: R$ 53.72
```

---

## Patterns utilizados

### 1. Strategy — políticas de desconto

**Problema resolvido:** o desconto varia conforme o cliente (idoso, fidelidade,
nenhum). Sem o padrão, isso viraria um `if/else` dentro da Venda, que precisaria
ser alterado a cada nova regra. Com o Strategy, cada política é uma classe
isolada e intercambiável em tempo de execução.

**Classes envolvidas:**

| Papel | Classe |
|---|---|
| Strategy (interface) | `desconto.PoliticaDesconto` |
| Estratégias concretas | `desconto.SemDesconto`, `desconto.DescontoIdoso`, `desconto.DescontoFidelidade` |
| Contexto | `venda.Venda` (guarda uma `PoliticaDesconto` e delega o cálculo) |

A `Venda` chama `politicaDesconto.calcularDesconto(...)` sem saber qual regra
está ativa. Trocar a política é só passar outra implementação no Builder
(`.comDesconto(...)`).

### 2. Builder — montagem da venda

**Problema resolvido:** a Venda tem muitos campos, vários opcionais (entrega,
endereço, embrulho, observação) e combináveis de formas diferentes. Construtores
telescópicos ficariam confusos e frágeis. O Builder monta a venda passo a passo,
com métodos fluentes, e valida tudo no `build()`.

**Classes envolvidas:**

| Papel | Classe |
|---|---|
| Produto (resultado) | `venda.Venda` (construtor privado, imutável) |
| Builder | `venda.Venda.Builder` (classe interna estática) |

O cliente e ao menos um item são obrigatórios; os demais são opcionais. O
`build()` recusa venda sem itens e entrega sem endereço, garantindo que a Venda
só nasce em estado válido.

### Como os dois se encontram

O `Venda.Builder` recebe a `PoliticaDesconto` (Strategy) via `.comDesconto(...)`
e a guarda na Venda. Assim o Builder monta o objeto e a Strategy define seu
comportamento de cálculo — cada padrão cuidando de uma responsabilidade.

---

## Estrutura do projeto

```
src/main/java/br/com/farmacia/
├── app/
│   └── Main.java              # menu de console e fluxo de uso
├── model/
│   ├── Produto.java           # entidade
│   ├── Cliente.java           # entidade
│   └── ItemVenda.java         # produto + quantidade
├── desconto/                  # PADRÃO STRATEGY
│   ├── PoliticaDesconto.java  # interface
│   ├── SemDesconto.java
│   ├── DescontoIdoso.java
│   └── DescontoFidelidade.java
└── venda/                     # PADRÃO BUILDER
    ├── Venda.java             # entidade + Builder interno
    └── Cupom.java             # formatação do cupom
```

---

## Declaração de uso de IA

Conforme as regras da Parte 1, declaramos o uso de IA:

- **Modelo utilizado:** Claude (Anthropic).
- **Partes em que foi utilizada:** apoio na estruturação do projeto Maven, na
  escrita do código dos padrões Strategy e Builder, na redação dos comentários e
  deste README, e na criação dos testes manuais. Todo o código foi revisado,
  testado e compreendido pela equipe.

---

## Contribuição de cada integrante

> Preencha com os nomes reais e o que cada um fez. Lembre que cada integrante
> precisa de pelo menos um commit autoral com a própria conta do GitHub.

| Integrante | Contribuição |
|---|---|
| Victor Casagrande Correa | Entidades (Produto, Cliente, ItemVenda) e catálogo |
| Isaac Alexsander Pereira Pessoa | Padrão Strategy (políticas de desconto) |
| Lucas Hoffmann | Padrão Builder (Venda e Builder) e cupom |
| Davi Novakoski | Fluxo de console (Main), testes e README |
