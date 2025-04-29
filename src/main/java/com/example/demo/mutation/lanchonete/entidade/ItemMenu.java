package com.example.demo.mutation.lanchonete.entidade;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ItemMenu {

  private String nome;
  private BigDecimal valor;
  // ex. item queijo, item principal = sanduiche
  private String nomeItemPrincipal;
  // ex. item combo, itens do combo = sanduiche, suco
  private List<String> nomeItensCombo;

}
