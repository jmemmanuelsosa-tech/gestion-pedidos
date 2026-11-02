package com.gestion.pedidos.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String mensaje,
        List<String> errores,
        LocalDateTime fecha
) {

    public ErrorResponse(int status, String mensaje) {
        this(status, mensaje, List.of(), LocalDateTime.now());
    }
}