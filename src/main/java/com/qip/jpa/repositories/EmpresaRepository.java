package com.qip.jpa.repositories;


import com.qip.jpa.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByCuit(String cuit);
    Optional<Empresa> findByNombre(String nombre);
}