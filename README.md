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

1. O usuário informa o cliente (nome, idade, fidelidade). Nome em branco ou
   idade fora de 0 a 130 anos fazem a pergunta se repetir.
2. Seleciona produtos do catálogo e as quantidades. Produto que exige receita
   (ex.: Amoxicilina) só entra se o cliente apresentar a receita, cuja
   identificação (CRM/número) é registrada na venda. Digitar `0` sem nenhum
   item avisa "Adicione ao menos um item." e continua no catálogo.
3. Escolhe a política de desconto (Strategy). Opção inválida repete a
   pergunta; se o cliente não for elegível para a política escolhida, o
   sistema avisa e a venda segue sem desconto.
4. Define o formato da venda (Builder): entrega (com endereço obrigatório),
   embrulho para presente e uma observação opcional.
5. O sistema valida, monta a venda e imprime o cupom com o total.

Se a entrada terminar (fim do pipe ou Ctrl+D/Ctrl+Z), o programa encerra com
"Até logo!" em vez de lançar erro.

---

## Tecnologias e pré-requisitos

- **JDK 17 ou superior** (compilação configurada para Java 17)
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

Os testes não usam framework e são executados pela classe `TesteManual`.
Com Maven:

```bash
mvn test-compile
java -cp target/classes:target/test-classes br.com.farmacia.TesteManual
```

Sem Maven:

```bash
javac -encoding UTF-8 -d target/classes $(find src/main -name "*.java")
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/br/com/farmacia/TesteManual.java
java -cp target/classes:target/test-classes br.com.farmacia.TesteManual
```

(no Windows, troque `:` por `;` no classpath)

As verificações cobrem os descontos e a elegibilidade de cada política, os
limites de idade, a taxa de entrega, a formatação do cupom, a validação das
entidades, a exigência de receita, os dados obrigatórios do Builder e a
imutabilidade das vendas ao reutilizá-lo.
O processo termina com código 1 se alguma verificação falhar.

---

## Exemplos de uso

Demonstração (opção 1) — a mesma cesta vendida com descontos e formatos diferentes:

```
=======================================
         FARMÁCIA SAÚDE & BEM
=======================================
Cliente: João (70 anos)
---------------------------------------
2x Dipirona 500mg = R$ 25,80
1x Vitamina C = R$ 25,00
---------------------------------------
Subtotal.........: R$ 50,80
Desconto idoso (15%): -R$ 7,62
Observação.......: Venda de balcão
---------------------------------------
TOTAL............: R$ 43,18
=======================================
```

A mesma cesta para uma cliente fidelidade, agora com entrega:

```
Cliente: Maria (30 anos) - cliente fidelidade
Subtotal.........: R$ 50,80
Desconto fidelidade (10%): -R$ 5,08
Taxa de entrega..: R$ 8,00
Endereço.........: Rua das Flores, 123
TOTAL............: R$ 53,72
```

Cliente sem desconto e venda para presente — sem desconto, o cupom não mostra
a linha de desconto:

```
Cliente: Ana (25 anos)
3x Vitamina C = R$ 75,00
Subtotal.........: R$ 75,00
Embrulho.........: presente
Observação.......: Presente de aniversário
TOTAL............: R$ 75,00
```

Medicamento controlado, vendido com receita:

```
Cliente: Ana (25 anos)
1x Amoxicilina 500mg = R$ 34,50
Subtotal.........: R$ 34,50
Receita.........: CRM-SC 12345
TOTAL............: R$ 34,50
```

Os valores são `BigDecimal` e o cupom usa sempre o formato brasileiro
(`R$ 43,18`), independentemente do idioma configurado na máquina.

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

Cada estratégia também responde `seAplica(cliente)`, dizendo se o cliente é
elegível: `SemDesconto` vale para todos, `DescontoIdoso` exige 60 anos ou mais
e `DescontoFidelidade` exige cadastro no programa. O `calcularDesconto` usa
essa regra (cliente não elegível recebe desconto zero), e o menu usa
`seAplica` para avisar o usuário e trocar para `SemDesconto` antes de fechar
a venda. O desconto é calculado em `BigDecimal` e arredondado para 2 casas com
`RoundingMode.HALF_EVEN`.

### 2. Builder — montagem da venda

**Problema resolvido:** a Venda tem muitos campos, vários opcionais (entrega,
endereço, embrulho, observação, receita) e combináveis de formas diferentes. Construtores
telescópicos ficariam confusos e frágeis. O Builder monta a venda passo a passo,
com métodos fluentes, e valida tudo no `build()`.

**Classes envolvidas:**

| Papel | Classe |
|---|---|
| Produto (resultado) | `venda.Venda` (construtor privado, imutável) |
| Builder | `venda.Venda.Builder` (classe interna estática) |

O cliente e ao menos um item são obrigatórios; os demais são opcionais. O
`build()` recusa venda sem itens, entrega sem endereço e produto que exige
receita sem a identificação da receita (`.comReceita("CRM-SC 12345")`),
garantindo que a Venda só nasce em estado válido. As entidades também se
protegem: `Cliente` recusa nome em branco e idade fora de 0 a 130, `Produto`
recusa nome em branco e preço menor ou igual a zero, e `ItemVenda` recusa
produto nulo e quantidade menor ou igual a zero.
Cada venda recebe uma cópia imutável dos itens, então reutilizar o Builder
para montar outra venda não altera os itens nem os totais das anteriores.

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
├── venda/                     # PADRÃO BUILDER
│   ├── Venda.java             # entidade + Builder interno
│   └── Cupom.java             # formatação do cupom
└── util/
    └── Moeda.java             # formatação de valores em R$ (pt-BR)
```

---

## Declaração de uso de IA

Conforme as regras da Parte 1, declaramos o uso de IA:

- **Ferramentas de IA utilizadas:** Claude (Anthropic) e Codex (OpenAI).
- **Partes em que foi utilizada:** a IA serviu como apoio no desenvolvimento,
  auxiliando na organização da estrutura do projeto e na escrita do código.
  Todo o conteúdo gerado foi revisado e compreendido pela equipe antes de ser
  incorporado ao trabalho.
- **Revisão com Codex:** apoio na revisão geral, na correção da imutabilidade
  das vendas e da configuração de compilação, e na ampliação dos testes.

---

## Contribuição de cada integrante

| Integrante | Contribuição |
|---|---|
| Victor Casagrande Correa | Desenvolvimento da Parte 1 |
| Isaac Alexsander Pereira Pessoa | Desenvolvimento da Parte 2 |
| Lucas Hoffmann | Desenvolvimento da Parte 2 |
| Davi Novakoski | Desenvolvimento da Parte 1 e da Parte 2; revisão do Builder, configuração de compilação e testes de regressão |
