package com.qip.jpa.controller;

import com.qip.jpa.entities.Cliente;
import com.qip.jpa.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> createClientes(@RequestBody List<Cliente> clientes) {
        List<Cliente> savedClientes = new ArrayList<>();

        try {
            for (Cliente cliente : clientes) {
                Cliente saved = clienteService.saveCliente(cliente);
                savedClientes.add(saved);
            }
            return ResponseEntity.ok(savedClientes);

        } catch (DataIntegrityViolationException ex) {
            Throwable rootCause = ex.getRootCause();
            if (rootCause != null && rootCause.getMessage().contains("duplicate key value")) {
                Pattern pattern = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\) already exists");
                Matcher matcher = pattern.matcher(rootCause.getMessage());
                if (matcher.find()) {
                    String campo = matcher.group(1);
                    String valor = matcher.group(2);
                    return ResponseEntity.badRequest().body("Ya existe un cliente con " + campo + " = '" + valor + "'.");
                }
                return ResponseEntity.badRequest().body("Ya existe un registro duplicado.");
            }
            return ResponseEntity.status(500).body("Error en la base de datos.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente) {
        try {
            Cliente saved = clienteService.saveCliente(cliente);
            return ResponseEntity.ok(saved);

        } catch (DataIntegrityViolationException ex) {
            Throwable rootCause = ex.getRootCause();
            if (rootCause != null && rootCause.getMessage().contains("duplicate key value")) {
                Pattern pattern = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\) already exists");
                Matcher matcher = pattern.matcher(rootCause.getMessage());
                if (matcher.find()) {
                    String campo = matcher.group(1);
                    String valor = matcher.group(2);
                    return ResponseEntity.badRequest().body("Ya existe un cliente con " + campo + " = '" + valor + "'.");
                }
                return ResponseEntity.badRequest().body("Ya existe un registro duplicado.");
            }
            return ResponseEntity.status(500).body("Error en la base de datos.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllClientes() {
        return ResponseEntity.ok(clienteService.getAllClientes());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getClienteById(@PathVariable Long id) {
        return clienteService.getClienteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.ok("Cliente eliminado");
    }
}
