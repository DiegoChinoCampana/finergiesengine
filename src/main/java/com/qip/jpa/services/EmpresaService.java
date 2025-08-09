package com.qip.jpa.services;


import com.qip.jpa.entities.Cliente;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.Role;
import com.qip.jpa.entities.User;
import com.qip.jpa.repositories.EmpresaRepository;
import com.qip.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UserRepository userRepository;

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