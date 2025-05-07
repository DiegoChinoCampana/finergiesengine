package com.qip.jpa.services;

import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.repositories.DatosDeBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DatosDeBalanceService {

    @Autowired
    private DatosDeBalanceRepository datosDeBalanceRepository;

    public Optional<DatosDeBalance> getDatosDeBalanceById(Long id) {
        return datosDeBalanceRepository.findById(id);
    }

    public DatosDeBalance saveDatosDeBalance(DatosDeBalance datosDeBalance) {
        return datosDeBalanceRepository.save(datosDeBalance);
    }
}