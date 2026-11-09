package com.gestion.pedidos.service;

import com.gestion.pedidos.dto.ItemPedidoRequest;
import com.gestion.pedidos.dto.PedidoRequest;
import com.gestion.pedidos.dto.PedidoResponse;
import com.gestion.pedidos.exception.NegocioException;
import com.gestion.pedidos.exception.RecursoNoEncontradoException;
import com.gestion.pedidos.model.*;
import com.gestion.pedidos.repository.PedidoRepository;
import com.gestion.pedidos.repository.ProductoRepository;
import com.gestion.pedidos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private Producto producto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("juan@test.com");
        usuario.setRol(Rol.CLIENTE);

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Hamburguesa");
        producto.setPrecio(new BigDecimal("8500"));
        producto.setStock(10);
    }

    @Test
    void crearPedido_conStock_descuentaStockYCalculaTotal() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoRequest request = new PedidoRequest(List.of(new ItemPedidoRequest(1L, 2)));

        PedidoResponse respuesta = pedidoService.crear("juan@test.com", request);

        assertThat(respuesta.total()).isEqualByComparingTo("17000");
        assertThat(respuesta.estado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(producto.getStock()).isEqualTo(8);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void crearPedido_sinStock_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        PedidoRequest request = new PedidoRequest(List.of(new ItemPedidoRequest(1L, 50)));

        assertThatThrownBy(() -> pedidoService.crear("juan@test.com", request))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crearPedido_productoInexistente_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        PedidoRequest request = new PedidoRequest(List.of(new ItemPedidoRequest(99L, 1)));

        assertThatThrownBy(() -> pedidoService.crear("juan@test.com", request))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void cancelarPedido_devuelveElStock() {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.agregarDetalle(new DetallePedido(producto, 3));
        producto.setStock(7);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponse respuesta = pedidoService.cambiarEstado(1L, EstadoPedido.CANCELADO);

        assertThat(respuesta.estado()).isEqualTo(EstadoPedido.CANCELADO);
        assertThat(producto.getStock()).isEqualTo(10);
    }

    @Test
    void cambiarEstado_pedidoEntregado_lanzaExcepcion() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.ENTREGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.cambiarEstado(1L, EstadoPedido.PENDIENTE))
                .isInstanceOf(NegocioException.class);
    }

    @Test
    void buscarPedido_deOtroCliente_lanzaExcepcion() {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.buscarPorId(1L, "otro@test.com", false))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}