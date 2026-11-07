package com.gestion.pedidos.dto;

import com.gestion.pedidos.model.Rol;
import com.gestion.pedidos.model.Usuario;

public record UsuarioResponse(Long id, String nombre, String email, Rol rol) {

    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}