package com.qip.jpa.controller;

import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.Industria;
import com.qip.jpa.entities.PostBalance;
import com.qip.jpa.services.EmpresaService;
import com.qip.jpa.services.IndustriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

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
    @PostMapping
    public ResponseEntity<?> createEmpresa(@RequestBody Empresa empresa) {
        try {
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
            return ResponseEntity.ok(savedEmpresa);

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
}
