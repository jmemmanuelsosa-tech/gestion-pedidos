package com.gestion.pedidos.controller;

import com.gestion.pedidos.dto.CambioEstadoRequest;
import com.gestion.pedidos.dto.MensajeIAResponse;
import com.gestion.pedidos.dto.PedidoRequest;
import com.gestion.pedidos.dto.PedidoResponse;
import com.gestion.pedidos.service.AsistenteIAService;
import com.gestion.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos", description = "Gestión de pedidos") 
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final AsistenteIAService asistenteIAService;

    public PedidoController(PedidoService pedidoService, AsistenteIAService asistenteIAService) {
        this.pedidoService = pedidoService;
        this.asistenteIAService = asistenteIAService;
    }

    // El usuario se obtiene del login, no del body
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request,
                                                Authentication authentication) {
        PedidoResponse pedido = pedidoService.crear(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponse>> misPedidos(Authentication authentication) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id,
                                                      Authentication authentication) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id, authentication.getName(), esAdmin(authentication)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponse> cambiarEstado(@PathVariable Long id,
                                                        @Valid @RequestBody CambioEstadoRequest request) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, request.estado()));
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(rol -> rol.getAuthority().equals("ROLE_ADMIN"));
    }
    
    @GetMapping("/{id}/mensaje-ia")
    public ResponseEntity<MensajeIAResponse> mensajeIA(@PathVariable Long id,
                                                       Authentication authentication) {
        return ResponseEntity.ok(
                asistenteIAService.generarMensaje(id, authentication.getName(), esAdmin(authentication)));
    }
}