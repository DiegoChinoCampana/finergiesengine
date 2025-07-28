package com.qip.jpa.controller;

import com.qip.jpa.services.FinancialReportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class FinancialReportController {

    @Autowired
    private FinancialReportBuilder reportGenerator;


    @GetMapping("/empresa/{empresa}/ejercicio/{anio}")
    public ResponseEntity<?> generarReporteFinanciero(
            @PathVariable String empresa,
            @PathVariable int anio) {
        try {
            String outputPath = "output/reporte_" + empresa + "_" + anio + ".pdf";

            reportGenerator.generateReportByEmpresaAndEjercicio(empresa, anio, outputPath);

            FileSystemResource file = new FileSystemResource(outputPath);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte.");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(file.getFilename())
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(file);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}