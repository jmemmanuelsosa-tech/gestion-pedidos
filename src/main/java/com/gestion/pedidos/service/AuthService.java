package com.gestion.pedidos.service;

import com.gestion.pedidos.dto.RegistroRequest;
import com.gestion.pedidos.dto.UsuarioResponse;
import com.gestion.pedidos.exception.NegocioException;
import com.gestion.pedidos.model.Rol;
import com.gestion.pedidos.model.Usuario;
import com.gestion.pedidos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        String email = request.email().toLowerCase().trim();

        if (usuarioRepository.existsByEmail(email)) {
            throw new NegocioException("Ya existe un usuario con ese email");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.password()));
        // Los registros públicos siempre son CLIENTE
        usuario.setRol(Rol.CLIENTE);

        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }
}