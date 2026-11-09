package com.gestion.pedidos.dto;

public record MensajeIAResponse(Long pedidoId, String mensaje, boolean generadoPorIA) {
}