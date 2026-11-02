package com.gestion.pedidos.dto;

import com.gestion.pedidos.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(

        @NotNull(message = "El estado es obligatorio")
        EstadoPedido estado
) {
}