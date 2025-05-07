package com.qip.jpa.services;


import com.qip.jpa.entities.Empresa;
import com.qip.jpa.repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public Empresa saveEmpresa(Empresa empresa) {
        return empresaRepository.save(empresa);
    }
    public Empresa findByCuit(String cuit) {
        return empresaRepository.findByCuit(cuit).orElse(null);
    }
    public Empresa findByNombre(String nombre) {
        return empresaRepository.findByNombre(nombre).orElse(null);
    }
}