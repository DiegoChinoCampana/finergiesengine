package com.qip.jpa.repositories;

import com.qip.jpa.entities.Industria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndustriaRepository extends JpaRepository<Industria, Long> {
    public Industria findByNombre(String nombre);

}
