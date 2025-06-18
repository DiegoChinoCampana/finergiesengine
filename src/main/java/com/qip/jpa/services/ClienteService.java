package com.qip.jpa.services;

import com.qip.jpa.entities.Cliente;
import com.qip.jpa.repositories.ClienteRepository;
import com.qip.jpa.repositories.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente saveCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente updateCliente(Long id, Cliente updatedCliente) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNombre(updatedCliente.getNombre());
            cliente.setCuit(updatedCliente.getCuit());
            cliente.setDireccion(updatedCliente.getDireccion());
            cliente.setTelefono(updatedCliente.getTelefono());
            cliente.setPersonaDeContacto(updatedCliente.getPersonaDeContacto());
            return clienteRepository.save(cliente);
        }).orElseThrow(() -> new EntityNotFoundException("Cliente con id " + id + " no encontrado"));
    }

    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente con id " + id + " no encontrado");
        }
        clienteRepository.deleteById(id);
    }


}
