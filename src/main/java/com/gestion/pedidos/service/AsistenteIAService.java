package com.gestion.pedidos.service;

import com.gestion.pedidos.dto.MensajeIAResponse;
import com.gestion.pedidos.dto.PedidoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AsistenteIAService {

    private static final Logger log = LoggerFactory.getLogger(AsistenteIAService.class);

    private static final String INSTRUCCIONES = """
            Sos el asistente de atención al cliente de un comercio argentino.
            Escribí un mensaje breve (máximo 3 oraciones) y cordial para informarle
            al cliente el estado de su pedido. No inventes datos. No uses markdown.
            """;

    private final ChatClient chatClient;
    private final PedidoService pedidoService;

    public AsistenteIAService(ChatClient.Builder chatClientBuilder, PedidoService pedidoService) {
        this.chatClient = chatClientBuilder.build();
        this.pedidoService = pedidoService;
    }

    public MensajeIAResponse generarMensaje(Long pedidoId, String email, boolean esAdmin) {
        // Reutiliza la validación de permisos del PedidoService
        PedidoResponse pedido = pedidoService.buscarPorId(pedidoId, email, esAdmin);

        try {
            String mensaje = chatClient.prompt()
                    .system(INSTRUCCIONES)
                    .user(armarDatosPedido(pedido))
                    .call()
                    .content();

            return new MensajeIAResponse(pedidoId, mensaje, true);
        } catch (Exception e) {
            // Si la IA falla, se responde con un mensaje predefinido
            log.warn("No se pudo generar el mensaje con IA: {}", e.getMessage());
            return new MensajeIAResponse(pedidoId, mensajePorDefecto(pedido), false);
        }
    }

    private String armarDatosPedido(PedidoResponse pedido) {
        String productos = pedido.detalles().stream()
                .map(d -> d.cantidad() + " x " + d.producto())
                .collect(Collectors.joining(", "));

        return "Pedido #" + pedido.id()
                + ". Estado: " + pedido.estado()
                + ". Productos: " + productos
                + ". Total: $" + pedido.total();
    }

    private String mensajePorDefecto(PedidoResponse pedido) {
        return "Hola, tu pedido #" + pedido.id() + " se encuentra en estado "
                + pedido.estado() + ". Total: $" + pedido.total() + ". ¡Gracias por tu compra!";
    }
}