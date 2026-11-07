package com.gestion.pedidos.service;

import com.gestion.pedidos.dto.ItemPedidoRequest;
import com.gestion.pedidos.dto.PedidoRequest;
import com.gestion.pedidos.dto.PedidoResponse;
import com.gestion.pedidos.exception.NegocioException;
import com.gestion.pedidos.exception.RecursoNoEncontradoException;
import com.gestion.pedidos.model.DetallePedido;
import com.gestion.pedidos.model.EstadoPedido;
import com.gestion.pedidos.model.Pedido;
import com.gestion.pedidos.model.Producto;
import com.gestion.pedidos.model.Usuario;
import com.gestion.pedidos.repository.PedidoRepository;
import com.gestion.pedidos.repository.ProductoRepository;
import com.gestion.pedidos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Si algo falla, se revierte todo (incluido el stock descontado)
    @Transactional
    public PedidoResponse crear(String email, PedidoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        for (ItemPedidoRequest item : request.items()) {
            Producto producto = productoRepository.findById(item.productoId())
                    .filter(Producto::getActivo)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado con id " + item.productoId()));

            if (producto.getStock() < item.cantidad()) {
                throw new NegocioException("Stock insuficiente para " + producto.getNombre()
                        + ". Disponible: " + producto.getStock());
            }

            producto.setStock(producto.getStock() - item.cantidad());
            pedido.agregarDetalle(new DetallePedido(producto, item.cantidad()));
        }

        return PedidoResponse.fromEntity(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorUsuario(String email) {
        return pedidoRepository.findByUsuarioEmailOrderByFechaDesc(email)
                .stream()
                .map(PedidoResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(PedidoResponse::fromEntity)
                .toList();
    }

    // Un cliente solo puede ver sus propios pedidos
    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id, String email, boolean esAdmin) {
        Pedido pedido = obtenerPedido(id);

        if (!esAdmin && !pedido.getUsuario().getEmail().equals(email)) {
            throw new RecursoNoEncontradoException("Pedido no encontrado con id " + id);
        }
        return PedidoResponse.fromEntity(pedido);
    }

    @Transactional
    public PedidoResponse cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPedido(id);
        EstadoPedido estadoActual = pedido.getEstado();

        if (estadoActual == EstadoPedido.ENTREGADO || estadoActual == EstadoPedido.CANCELADO) {
            throw new NegocioException("No se puede modificar un pedido " + estadoActual);
        }

        // Si se cancela, se devuelve el stock
        if (nuevoEstado == EstadoPedido.CANCELADO) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
            }
        }

        pedido.setEstado(nuevoEstado);
        return PedidoResponse.fromEntity(pedidoRepository.save(pedido));
    }

    private Pedido obtenerPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id " + id));
    }
}