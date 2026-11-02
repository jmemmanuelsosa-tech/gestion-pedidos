package com.gestion.pedidos.dto;

import com.gestion.pedidos.model.EstadoPedido;
import com.gestion.pedidos.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        String cliente,
        LocalDateTime fecha,
        EstadoPedido estado,
        BigDecimal total,
        List<DetallePedidoResponse> detalles
) {

    public static PedidoResponse fromEntity(Pedido pedido) {
        List<DetallePedidoResponse> detalles = pedido.getDetalles()
                .stream()
                .map(DetallePedidoResponse::fromEntity)
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getUsuario().getEmail(),
                pedido.getFecha(),
                pedido.getEstado(),
                pedido.getTotal(),
                detalles
        );
    }
}