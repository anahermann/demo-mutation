# EXERCÍCIO DE MELHORIA DE TESTES UNITÁRIOS

Esse sistema de lanchonete tem problemas, o que é estranho porque a cobertura de testes está em 92% e todos os teste passam.

O exercício trata-se de identificar e corrigir os problemas, utilizando como apoio a ferramenta [PIT](https://pitest.org/) para [testes de mutação](https://pt.wikipedia.org/wiki/Teste_de_muta%C3%A7%C3%A3o), entendendo assim as vantagens e limitações da ferramenta para criação de testes mais robustos.

Testes de mutação são testes que modificam o código da aplicação e rodam novamente os testes unitários, identificando três situações possíveis: mutação eliminada (killed), mutação não detectada (survived) ou sem cobertura (no coverage), o que permite uma análise mais detalhada do que a métrica de cobertura usual.

## Regras do sistema
O sistema calcula o valor de uma compra numa lanchonete de acordo com o cardápio, regras e descontos.

- A saída do cálculo é no formato `R$ 9,99`
- Formas de pagamento válidas: PIX, dinheiro, debito, credito
- Pagamento em dinheiro ou PIX tem 5% de desconto
- Pagamento a crédito tem acréscimo de 3% no valor total
- Se não forem pedidos itens, apresentar mensagem "Não há itens no carrinho de compra!"
- Se a quantidade de itens for zero, apresentar mensagem "Quantidade inválida!"
- Se o código do item não existir, apresentar mensagem "Item inválido!"
- Se a forma de pagamento não existir, apresentar mensagem "Forma de pagamento inválida!"
- Caso item extra seja informado num pedido que não tenha o respectivo item principal, apresentar mensagem "Item extra não pode ser pedido sem o principal!"
- É possível pedir mais de um item extra sem precisar de mais de um principal
- É possível pedir um item extra havendo item principal em um combo

## Entradas do sistema

O sistema recebe duas strings, `pedido` e  `formaPagamento`. O pedido é informado separando as quantidades por vírgula e itens diferentes por ponto e vírgula. Por exemplo, um café e dois salgados são representados por `cafe,1;salgado,2`.

## Dados

O retorno do banco de dados é mockado durante os testes. Por exemplo, para retornar a entidade correpondente a café, utiliza-se o código abaixo no respectivo teste:

```java
// inicializar os dados conforme desejado
ItemMenu meuDadoDeTeste = ItemMenu.builder().valor(BigDecimal.valueOf(3.00)).build();
// mock da chamada ao banco de dados
when(itemMenuRepository.getItem("cafe")).thenReturn(meuDadoDeTeste);
```

### Exemplo de cardápio

  | descrição                      | valor   |
  |--------------------------------|---------|
  | Café                           | R$ 3,00 |
  | Chantily (extra do Café)       | R$ 1,50 |
  | Suco                           | R$ 6,20 |
  | Sanduíche                      | R$ 6,50 |
  | Queijo (extra do Sanduíche)    | R$ 2,00 |
  | Salgado                        | R$ 7,25 |
  | Combo 1 (1 Suco e 1 Sanduíche) | R$ 9,50 |
  | Combo 2 (1 Café e 1 Sanduíche) | R$ 7,50 |

## Instalação

mvn install

## Rodando o PIT

mvn test -P coverage

O relatório em formato HTML será gerado na pasta /target/pit-reports/ do projeto. Caso você tenha problemas para instalar ou executar, o pdf do relatório gerado para a versão inicial da classe principal encontra-se [anexado](https://github.com/anahermann/demo-mutation/blob/main/RelatorioPIT.pdf).

## Analisando o relatório

O relatório mostra as linhas que foram modificadas, qual modificação foi feita e qual o resultado de cada modificação.
