package com.gestion.pedidos.config;

import com.gestion.pedidos.model.Producto;
import com.gestion.pedidos.model.Rol;
import com.gestion.pedidos.model.Usuario;
import com.gestion.pedidos.repository.ProductoRepository;
import com.gestion.pedidos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           ProductoRepository productoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRol(Rol.ADMIN);
            usuarioRepository.save(admin);
        }

        if (productoRepository.count() == 0) {
            crearProducto("Hamburguesa clásica", "Carne, queso, lechuga y tomate", "8500", 50);
            crearProducto("Papas fritas", "Porción grande", "4200", 80);
            crearProducto("Gaseosa 500ml", "Sabor a elección", "2000", 100);
        }
    }

    private void crearProducto(String nombre, String descripcion, String precio, int stock) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(new BigDecimal(precio));
        producto.setStock(stock);
        productoRepository.save(producto);
    }
}