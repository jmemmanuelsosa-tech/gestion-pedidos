package com.gestion.pedidos.repository;

import com.gestion.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioEmailOrderByFechaDesc(String email);
}