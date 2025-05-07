package com.qip.jpa.repositories;

import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.PostBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface PostBalanceRepository extends JpaRepository<PostBalance, Long> {
    @Query("SELECT p FROM PostBalance p WHERE p.empresa = :empresa AND EXTRACT(YEAR FROM p.ejercicio) = :anio")
    Optional<PostBalance> findByEmpresaAndAnio(@Param("empresa") Empresa empresa, @Param("anio") int anio);

}