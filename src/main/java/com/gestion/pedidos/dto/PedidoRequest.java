package com.gestion.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PedidoRequest(

        // @Valid valida también cada item de la lista
        @NotEmpty(message = "El pedido debe tener al menos un producto")
        List<@Valid ItemPedidoRequest> items
) {
}