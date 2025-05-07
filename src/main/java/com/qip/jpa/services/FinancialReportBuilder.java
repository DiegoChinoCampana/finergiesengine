package com.qip.jpa.services;

import com.qip.engine.FinancialReportService;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.PostBalance;
import com.qip.jpa.repositories.EmpresaRepository;
import com.qip.jpa.repositories.DatosDeBalanceRepository;
import com.qip.jpa.repositories.PostBalanceRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class FinancialReportBuilder {
    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private DatosDeBalanceRepository datosDeBalanceRepository;

    @Autowired
    private PostBalanceRepository postBalanceRepository;

    public void generateReportByEmpresaAndEjercicio(String empresaNombre, int anioEjercicio, String outputPath) throws Exception {
        Optional<Empresa> optionalEmpresa = empresaRepository.findByNombre(empresaNombre);
        if (optionalEmpresa.isEmpty()) {
            throw new Exception("Empresa no encontrada con nombre: " + empresaNombre);
        }
        Empresa empresa = optionalEmpresa.get();

        DatosDeBalance balance1 = datosDeBalanceRepository.findByEmpresaAndAnio(empresa, anioEjercicio)
                .orElseThrow(() -> new Exception("Balance no encontrado para la empresa en el año " + anioEjercicio));

        PostBalance postBalance1 = postBalanceRepository.findByEmpresaAndAnio(empresa, anioEjercicio)
                .orElseThrow(() -> new Exception("PostBalance no encontrado para la empresa en el año " + anioEjercicio));

        DatosDeBalance balance2 = datosDeBalanceRepository.findByEmpresaAndAnio(empresa, anioEjercicio-1)
                .orElseThrow(() -> new Exception("Balance no encontrado para la empresa en el año " + (anioEjercicio-1)));

        PostBalance postBalance2 = postBalanceRepository.findByEmpresaAndAnio(empresa, anioEjercicio-1)
                .orElseThrow(() -> new Exception("PostBalance no encontrado para la empresa en el año " + (anioEjercicio-1)));


        // Generar el reporte
        FinancialReportService reportService = new FinancialReportService();
        reportService.generateFinancialReport(empresa, balance1,balance2,postBalance1,postBalance2, outputPath);

        System.out.println("Reporte generado exitosamente en: " + outputPath);
    }
}
