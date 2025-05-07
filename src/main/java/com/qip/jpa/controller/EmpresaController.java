package com.qip.jpa.controller;

import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.Industria;
import com.qip.jpa.entities.PostBalance;
import com.qip.jpa.services.EmpresaService;
import com.qip.jpa.services.IndustriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private IndustriaService industriaService;

    @PostMapping("/batch")
    public List<Empresa> createEmpresas(@RequestBody List<Empresa> empresas) {
        List<Empresa> savedEmpresas = new ArrayList<>();
        for (Empresa empresa : empresas) {
            // Fetch the Industria by its name
            String industriaNombre = empresa.getIndustria().getNombre();
            Industria industria = industriaService.getIndustriaByNombre(industriaNombre);

            // Associate the fetched Industria with the Empresa
            empresa.setIndustria(industria);

            // Asegurar relaciones inversas
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
            // Save the Empresa
            Empresa savedEmpresa = empresaService.saveEmpresa(empresa);
            savedEmpresas.add(savedEmpresa);
        }
        return savedEmpresas;
    }

}
