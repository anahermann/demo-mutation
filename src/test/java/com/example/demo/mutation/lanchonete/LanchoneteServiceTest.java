package com.example.demo.mutation.lanchonete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;

import com.example.demo.mutation.lanchonete.entidade.ItemMenu;
import com.example.demo.mutation.lanchonete.repository.ItemMenuRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class LanchoneteServiceTest {

  @Spy
  @InjectMocks
  private LanchoneteService service;

  @Mock
  private ItemMenuRepository itemMenuRepository;

  @BeforeEach
  public void before() {

    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCalculaValorCompraSimples() {

    when(itemMenuRepository.getItem("cafe"))
        .thenReturn(ItemMenu.builder().valor(BigDecimal.valueOf(3.00)).build());
    Mockito.doReturn("R$ 2,85").when(service).formataValor(Mockito.any());
    String resultado = service.calculaValor("cafe,1", "dinheiro");
    assertEquals("R$ 2,85", resultado);
  }

  @Test
  void testCalculaValorCompraComExtra() {

    when(itemMenuRepository.getItem("sanduiche"))
        .thenReturn(ItemMenu.builder().valor(BigDecimal.valueOf(6.50)).build());
    when(itemMenuRepository.getItem("queijo"))
        .thenReturn(ItemMenu.builder().valor(BigDecimal.valueOf(2.00)).build());

    String resultado = service.calculaValor("sanduiche,1;queijo,1", "credito");
    assertNotNull(resultado);
  }

  @Test
  void testCalculaValorComFormaPagamentoInvalida() {

    assertThrows(IllegalArgumentException.class,
        () -> service.calculaValor("sanduiche,1", "crebito"));
  }

  @Test
  void testCalculaValorComItemInvalido() {

    when(itemMenuRepository.getItem("sushi")).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> service.calculaValor("sushi,1", "dinheiro"));
  }

  @Test
  void testCalculaValorComQuantidadeInvalida() {

    when(itemMenuRepository.getItem("cafe"))
        .thenReturn(ItemMenu.builder().valor(BigDecimal.valueOf(3.00)).build());
    assertThrows(IllegalArgumentException.class, () -> service.calculaValor("cafe,0", "dinheiro"));
  }

  @Test
  void testCalculaValorSemItens() {

    assertThrows(IllegalArgumentException.class, () -> service.calculaValor("", "dinheiro"));
  }

  @Test
  void testCalculaValorComboComItemExtra() {
    when(itemMenuRepository.getItem("combo1")).thenReturn(ItemMenu.builder()
        .valor(BigDecimal.valueOf(9.50)).nomeItensCombo(Arrays.asList("sanduiche", "suco")).build());
    when(itemMenuRepository.getItem("queijo")).thenReturn(
        ItemMenu.builder().valor(BigDecimal.valueOf(2.00)).nomeItemPrincipal("sanduiche").build());

    String resultado = service.calculaValor("combo1,1;queijo,1", "dinheiro");
    assertEquals("R$ 10,92", resultado.replace(".", ","));

  }
}
