package com.example.demo.mutation.lanchonete.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ItemPedidoDTO {

  private String nome;
  private Long qtd;

}
