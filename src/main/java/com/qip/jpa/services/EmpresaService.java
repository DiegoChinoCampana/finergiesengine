package com.qip.jpa.services;


import com.qip.jpa.entities.Empresa;
import com.qip.jpa.repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public Empresa findById(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa con id " + id + " no encontrada"));
    }

    public Empresa saveEmpresa(Empresa empresa) {
        return empresaRepository.save(empresa);
    }
    public Empresa findByCuit(String cuit) {
        return empresaRepository.findByCuit(cuit).orElse(null);
    }
    public Empresa findByNombre(String nombre) {
        return empresaRepository.findByNombre(nombre).orElse(null);
    }

    public boolean existsByCuit(String cuit) {
        return empresaRepository.findByCuit(cuit).isPresent();
    }

    public boolean existsByNombre(String nombre) {
        return empresaRepository.findByNombre(nombre).isPresent();
    }

    public List<Empresa> getEmpresasByClienteId(Long clienteId) {
        return empresaRepository.findByClienteId(clienteId);
    }
}