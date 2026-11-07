package com.gestion.pedidos.service;

import com.gestion.pedidos.dto.ProductoRequest;
import com.gestion.pedidos.dto.ProductoResponse;
import com.gestion.pedidos.exception.RecursoNoEncontradoException;
import com.gestion.pedidos.model.Producto;
import com.gestion.pedidos.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Inyección por constructor 
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(ProductoResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        return ProductoResponse.fromEntity(obtenerProducto(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());

        return ProductoResponse.fromEntity(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerProducto(id);
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());

        return ProductoResponse.fromEntity(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = obtenerProducto(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .filter(Producto::getActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + id));
    }
}