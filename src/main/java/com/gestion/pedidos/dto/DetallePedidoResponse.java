package com.gestion.pedidos.dto;

import com.gestion.pedidos.model.DetallePedido;

import java.math.BigDecimal;

public record DetallePedidoResponse(
        Long productoId,
        String producto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {

    public static DetallePedidoResponse fromEntity(DetallePedido detalle) {
        return new DetallePedidoResponse(
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
        );
    }
}