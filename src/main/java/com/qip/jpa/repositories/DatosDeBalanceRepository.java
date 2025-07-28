package com.qip.jpa.repositories;

import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface DatosDeBalanceRepository extends JpaRepository<DatosDeBalance, Long> {
    @Query("SELECT d FROM DatosDeBalance d WHERE d.empresa = :empresa AND EXTRACT(YEAR FROM d.ejercicio) = :anio")
    Optional<DatosDeBalance> findByEmpresaAndAnio(@Param("empresa") Empresa empresa, @Param("anio") int anio);


}