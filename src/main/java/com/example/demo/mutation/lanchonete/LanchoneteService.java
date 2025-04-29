package com.example.demo.mutation.lanchonete;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.example.demo.mutation.lanchonete.dto.ItemPedidoDTO;
import com.example.demo.mutation.lanchonete.entidade.ItemMenu;
import com.example.demo.mutation.lanchonete.enums.FormaPagamentoEnum;
import com.example.demo.mutation.lanchonete.repository.ItemMenuRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanchoneteService {

  @Autowired
  private ItemMenuRepository itemMenuRepository;

  public String calculaValor(String pedido, String formaPagamento) {

    validarFormaPagamento(formaPagamento);
    validarEntrada(pedido);
    List<ItemPedidoDTO> listaItens = parseListaItens(pedido);
    validaListaItensMenu(listaItens);

    BigDecimal total = listaItens.stream().map(item -> getValorTotalItem(item))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal valorFinal = aplicaTaxa(formaPagamento, total);

    return formataValor(valorFinal);
  }

  protected String formataValor(BigDecimal valorFinal) {

    NumberFormat formatter = new DecimalFormat("#,###.##");
    return "R$ " + formatter.format(valorFinal);
  }

  private void validaListaItensMenu(List<ItemPedidoDTO> listaItens) {

    for (int i = 0; i < listaItens.size(); i++) {
      ItemPedidoDTO item = listaItens.get(i);
      ItemMenu itemMenu = getEntidadeItem(item);
      if (itemMenu == null) {
        throw new InvalidParameterException("Item inválido!");
      }
      if (item.getQtd() == 0) {
        throw new InvalidParameterException("Quantidade inválida!");
      }
      if (itemMenu.getNomeItemPrincipal()!=null && !existeItemPrincipal(listaItens, itemMenu)
      && !existeItemCombo(listaItens, itemMenu.getNomeItemPrincipal())) {
        throw new InvalidParameterException("Item extra não pode ser pedido sem o principal!");
      }
    }
  }

  private ItemMenu getEntidadeItem(ItemPedidoDTO item) {

    return itemMenuRepository.getItem(item.getNome());
  }

  private boolean existeItemCombo(List<ItemPedidoDTO> listaItens, String nome) {

    return listaItens.stream().anyMatch(item -> {
      ItemMenu itemMenu = getEntidadeItem(item);
      if (itemMenu.getNomeItensCombo().contains(nome)) {
        return true;
      }
      return false;
    });
  }

  private boolean existeItemPrincipal(List<ItemPedidoDTO> listaItens, ItemMenu itemMenu) {

    return listaItens.stream().anyMatch(item -> item.getNome().equals(itemMenu.getNome()));
  }

  private void validarEntrada(String entrada) {

    if (entrada.length() == 0) {
      throw new IllegalArgumentException("Não há itens no carrinho de compras!");
    }
  }

  private void validarFormaPagamento(String formaPagamento) {

    if (FormaPagamentoEnum.valueOf(formaPagamento.toUpperCase()) == null) {
      throw new IllegalArgumentException("Forma de pagamento inexistente!");
    }

  }

  private BigDecimal aplicaTaxa(String formaPagamento, BigDecimal total) {

    List<String> formasComDesconto = Arrays.asList("dinheiro", "PIX");
    if (formaPagamento.equals("credito")) {
      return total.multiply(BigDecimal.valueOf(1.03));
    } else if (formasComDesconto.contains(formaPagamento)) {
      return total.multiply(BigDecimal.valueOf(0.95));
    }
    return total;
  }

  private BigDecimal getValorTotalItem(ItemPedidoDTO item) {

    ItemMenu itemValor = getEntidadeItem(item);
    return BigDecimal.valueOf(item.getQtd()).multiply(itemValor.getValor());
  }

  private List<ItemPedidoDTO> parseListaItens(String entrada) {

    List<ItemPedidoDTO> listaItens = new ArrayList<>();
    String[] itens = entrada.split(";");
    for (String item : itens) {
      String[] dados = item.split(",");
      listaItens.add(ItemPedidoDTO.builder().nome(dados[0]).qtd(Long.valueOf(dados[1])).build());
    }
    return listaItens;
  }

}
