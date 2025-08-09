package com.qip.jpa.controller;

import com.qip.dtos.EmpresaDTO;
import com.qip.jpa.entities.*;
import com.qip.jpa.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private DatosDeBalanceService datosDeBalanceService;

    @Autowired
    private PostBalanceService postBalanceService;

    @Autowired
    private IndustriaService industriaService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> createEmpresas(@RequestBody List<Empresa> empresas) {
        List<Empresa> savedEmpresas = new ArrayList<>();

        try {
            for (Empresa empresa : empresas) {
                String industriaNombre = empresa.getIndustria().getNombre();
                Industria industria = industriaService.getIndustriaByNombre(industriaNombre);
                empresa.setIndustria(industria);

                if (empresa.getDatosDeBalances() != null) {
                    for (DatosDeBalance db : empresa.getDatosDeBalances()) {
                        db.setEmpresa(empresa);
                    }
                }

                if (empresa.getPostBalances() != null) {
                    for (PostBalance pb : empresa.getPostBalances()) {
                        pb.setEmpresa(empresa);
                    }
                }

                Empresa savedEmpresa = empresaService.saveEmpresa(empresa);
                savedEmpresas.add(savedEmpresa);
            }

            return ResponseEntity.ok(savedEmpresas);

        } catch (DataIntegrityViolationException ex) {
            Throwable rootCause = ex.getRootCause();
            if (rootCause != null && rootCause.getMessage().contains("duplicate key value")) {
                Pattern pattern = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\) already exists");
                Matcher matcher = pattern.matcher(rootCause.getMessage());
                if (matcher.find()) {
                    String campo = matcher.group(1);
                    String valor = matcher.group(2);
                    return ResponseEntity.badRequest().body("Ya existe una empresa con " + campo + " = '" + valor + "'.");
                }
                return ResponseEntity.badRequest().body("Ya existe un registro duplicado.");
            }
            return ResponseEntity.status(500).body("Error en la base de datos.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{empresaId}/balances")
    public ResponseEntity<?> agregarBalances(
            @PathVariable Long empresaId,
            @RequestBody Empresa empresaConBalances) {
        try {
            Empresa empresa = empresaService.findById(empresaId);
            if (empresa == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empresa no encontrada.");
            }

            if (empresaConBalances.getDatosDeBalances() != null) {
                for (DatosDeBalance db : empresaConBalances.getDatosDeBalances()) {
                    db.setEmpresa(empresa);
                    datosDeBalanceService.saveDatosDeBalance(db);
                }
            }

            if (empresaConBalances.getPostBalances() != null) {
                for (PostBalance pb : empresaConBalances.getPostBalances()) {
                    pb.setEmpresa(empresa);
                    postBalanceService.savePostBalance(pb);
                }
            }

            return ResponseEntity.ok("Balances agregados correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createEmpresa(@RequestBody Empresa empresa) {
        try {
            if (empresaService.existsByNombre(empresa.getNombre())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe una empresa con el nombre '" + empresa.getNombre() + "'.");
            }

            if (empresaService.existsByCuit(empresa.getCuit())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe una empresa con el CUIT '" + empresa.getCuit() + "'.");
            }

            String industriaNombre = empresa.getIndustria().getNombre();
            Industria industria = industriaService.getIndustriaByNombre(industriaNombre);
            empresa.setIndustria(industria);

            empresa.setDatosDeBalances(null);
            empresa.setPostBalances(null);

            Empresa savedEmpresa = empresaService.saveEmpresa(empresa);
            return ResponseEntity.ok(savedEmpresa);

        } catch (DataIntegrityViolationException ex) {
            return handleDuplicateKeyException(ex);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> getEmpresasByCliente(@PathVariable Long clienteId) {
        try {
            List<Empresa> empresas = empresaService.getEmpresasByClienteId(clienteId);
            return ResponseEntity.ok(empresas);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener las empresas: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<?> getEmpresasByUserId(@PathVariable Long userId) {
        try {
            Cliente cliente = userService.getClienteByUserId(userId);
            Long clientId = cliente.getId();

            List<Empresa> empresas = empresaService.getEmpresasByClienteId(clientId);
            // Convertir a DTOs
            List<EmpresaDTO> empresaDTOs = empresas.stream()
                    .map(EmpresaDTO::new)
                    .toList();

            return ResponseEntity.ok(empresaDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener las empresas: " + e.getMessage());
        }
    }




    private ResponseEntity<String> handleDuplicateKeyException(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause != null && rootCause.getMessage() != null && rootCause.getMessage().contains("duplicate key value")) {
            Pattern pattern = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\) already exists");
            Matcher matcher = pattern.matcher(rootCause.getMessage());
            if (matcher.find()) {
                String campo = matcher.group(1);
                String valor = matcher.group(2);
                return ResponseEntity.badRequest().body("Ya existe una empresa con " + campo + " = '" + valor + "'.");
            }
            return ResponseEntity.badRequest().body("Ya existe un registro duplicado.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos.");
    }

}
