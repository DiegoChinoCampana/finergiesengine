package com.qip.engine;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.PostBalance;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.Locale;



public class FinancialReportService {

    public void generateFinancialReport(Empresa empresa, DatosDeBalance balance1, DatosDeBalance balance2, PostBalance postBalance1, PostBalance postBalance2, String outputPath) throws Exception {
        File file = new File(outputPath);
        file.getParentFile().mkdirs();

        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Encabezado
        document.add(new Paragraph("Reporte Financiero - " + empresa.getNombre()).setBold().setFontSize(16));
        document.add(new Paragraph("CUIT: " + empresa.getCuit()));
        document.add(new Paragraph("Comparativo de ejercicios: " + balance1.getEjercicio() + " y " + balance2.getEjercicio()));
        document.add(new Paragraph("\n"));

        int year1 = balance1.getEjercicio().getYear();
        int year2 = balance2.getEjercicio().getYear();

        // Tabla de análisis económico
        document.add(new Paragraph("Análisis Económico").setBold().setFontSize(14));
        float[] econWidths = {300F, 150F, 150F};
        Table analisisEconomico = new Table(econWidths);
        addHeaderRow(analisisEconomico, String.valueOf(year1), String.valueOf(year2));

        addRow(analisisEconomico, "Ventas Netas", balance1.getVentasNetas(), balance2.getVentasNetas());
        addRow(analisisEconomico, "Resultado Bruto", balance1.getResultadoBruto(), balance2.getResultadoBruto());
        addRow(analisisEconomico, "Margen Bruto", (balance1.getResultadoBruto() / balance1.getVentasNetas()) * 100, (balance2.getResultadoBruto() / balance2.getVentasNetas()) * 100);
        addRow(analisisEconomico, "Resultado Operativo", balance1.getResultadoOperativo(), balance2.getResultadoOperativo());
        addRow(analisisEconomico, "Margen Operativo", (balance1.getResultadoOperativo() / balance1.getVentasNetas()) * 100, (balance2.getResultadoOperativo() / balance2.getVentasNetas()) * 100);

        double ebitda1 = balance1.getResultadoOperativo() + balance1.getAmortizacionesYDepreciaciones();
        double ebitda2 = balance2.getResultadoOperativo() + balance2.getAmortizacionesYDepreciaciones();
        addRow(analisisEconomico, "EBITDA", ebitda1, ebitda2);
        addRow(analisisEconomico, "Margen EBITDA", (ebitda1 / balance1.getVentasNetas()) * 100, (ebitda2 / balance2.getVentasNetas()) * 100);

        addRow(analisisEconomico, "Resultado Neto", balance1.getResultadoNeto(), balance2.getResultadoNeto());
        addRow(analisisEconomico, "Margen Neto", (balance1.getResultadoNeto() / balance1.getVentasNetas()) * 100, (balance2.getResultadoNeto() / balance2.getVentasNetas()) * 100);
        document.add(analisisEconomico);

        // Tabla de balance
        document.add(new Paragraph("\nBalance General").setBold().setFontSize(14));
        Table balanceTable = new Table(econWidths);
        addHeaderRow(balanceTable, String.valueOf(year1), String.valueOf(year2));

        addRow(balanceTable, "Activos Corrientes", balance1.getActivoCorriente(), balance2.getActivoCorriente());
        addRow(balanceTable, "Activos No Corrientes", balance1.getActivoNoCorriente(), balance2.getActivoNoCorriente());
        addRow(balanceTable, "Total de Activos", balance1.getActivoCorriente() + balance1.getActivoNoCorriente(), balance2.getActivoCorriente() + balance2.getActivoNoCorriente());
        addRow(balanceTable, "Pasivos Corrientes", balance1.getPasivoCorriente(), balance2.getPasivoCorriente());
        addRow(balanceTable, "Pasivos No Corrientes", balance1.getPasivoNoCorriente(), balance2.getPasivoNoCorriente());
        addRow(balanceTable, "Total del Pasivo", balance1.getPasivoCorriente() + balance1.getPasivoNoCorriente(), balance2.getPasivoCorriente() + balance2.getPasivoNoCorriente());
        addRow(balanceTable, "Patrimonio Neto", balance1.getPatrimonioNeto(), balance2.getPatrimonioNeto());
        document.add(balanceTable);

        // Tabla de ratios
        document.add(new Paragraph("\nAnálisis financiero").setBold().setFontSize(14));
        Table ratiosTable = new Table(econWidths);
        addHeaderRow(ratiosTable, String.valueOf(year1), String.valueOf(year2));

        double nfd1 = balance1.getDeudaBancariaTotal()  - balance1.getCajaEInversionesCorrientes();
        double nfd2 = balance2.getDeudaBancariaTotal()  - balance2.getCajaEInversionesCorrientes();
        double tds1 = balance1.getDeudaFinancieraPasivoCorriente() + balance1.getInteresesPagados();
        double tds2 = balance2.getDeudaFinancieraPasivoCorriente() + balance2.getInteresesPagados();

        addRow(ratiosTable, "Caja e Inversiones Corrientes", balance1.getCajaEInversionesCorrientes(), balance2.getCajaEInversionesCorrientes());
        addRow(ratiosTable, "Deuda Financiera", balance1.getDeudaFinancieraTotal(), balance2.getDeudaFinancieraTotal());
        addRow(ratiosTable, "Deuda Neta Financiera (NFD)", nfd1, nfd2);
        addRow(ratiosTable, "Servicio de Deuda Total (TDS)", tds1, tds2);

        //addRow(ratiosTable, "Cobertura de Intereses", balance1.getInteresesPagados() / ebitda1, balance2.getInteresesPagados() / ebitda2);
        addRow(ratiosTable, "Cobertura de Intereses".trim(),
                safeDivide(balance1.getInteresesPagados(), ebitda1),
                safeDivide(balance2.getInteresesPagados(), ebitda2));

        addRow(ratiosTable, "Ratio de Liquidez", balance1.getActivoCorriente() / balance1.getPasivoCorriente(), balance2.getActivoCorriente() / balance2.getPasivoCorriente());
        addRow(ratiosTable, "NFD/EBITDA", nfd1 / ebitda1, nfd2 / ebitda2);
        addRow(ratiosTable, "TDS/EBITDA", tds1 / ebitda1, tds2 / ebitda2);
        document.add(ratiosTable);

        // Ciclo de caja
        document.add(new Paragraph("\nCiclo Operativo en Dìas").setBold().setFontSize(14));
        Table cicloCaja = new Table(econWidths);
        addHeaderRow(cicloCaja, String.valueOf(year1), String.valueOf(year2));

        addRow(cicloCaja, "Periodo de Cobro", (balance1.getCreditosPorVentas() / balance1.getVentasNetas()) * 365, (balance2.getCreditosPorVentas() / balance2.getVentasNetas()) * 365);
        addRow(cicloCaja, "Periodo de Pagos", (balance1.getProveedores() / balance1.getCompras()) * 365, (balance2.getProveedores() / balance2.getCompras()) * 365);
        addRow(cicloCaja, "Rotación de Inventarios", (balance1.getMercaderias() / balance1.getCostoMercaderiasVendidas()) * 365, (balance2.getMercaderias() / balance2.getCostoMercaderiasVendidas()) * 365);

        double nct1 = balance1.getCreditosPorVentas() + balance1.getMercaderias() - balance1.getProveedores();
        double nct2 = balance2.getCreditosPorVentas() + balance2.getMercaderias() - balance2.getProveedores();
        double gap1 = nct1 - balance1.getDeudaBancariaPasivoCorriente();
        double gap2 = nct2 - balance2.getDeudaBancariaPasivoCorriente();
        addRow(cicloCaja, "GAP ", gap1, gap2);
        addRow(cicloCaja, "Necesidad de Capital de Trabajo", nct1, nct2);

        document.add(cicloCaja);

        // Ventas y Deuda Postbalance
        document.add(new Paragraph("\nAnálisis de Ventas y Deuda Postbalance").setBold().setFontSize(14));
        Table ventasDeudaTable = new Table(econWidths);
        addHeaderRow(ventasDeudaTable, "Post último ejercicio", String.valueOf(year2));

// Cálculos: Promedio de ventas mensuales postbalance
        double promedioVentasBalance1 = balance1.getVentasNetas() / 12;
        double promedioVentasBalance2 = balance2.getVentasNetas() / 12;


// Cálculos: Variación respecto al último balance (%)
        double variacion1 = (postBalance1.getPromedioVentasMensuales() != 0) ? (postBalance1.getPromedioVentasMensuales() / promedioVentasBalance1) * 100 : 0;
        double variacion2 = (postBalance2.getPromedioVentasMensuales() != 0) ? (postBalance2.getPromedioVentasMensuales() / promedioVentasBalance2) * 100 : 0;

// Texto de variación (ej: "incremento 39%")
        String textoVariacion1 = (variacion1 > 100) ? "incremento " + Math.round(variacion1 - 100) + "%" :
                (variacion1 < 100) ? "disminución " + Math.round(100 - variacion1) + "%" :
                        "sin variación";
        String textoVariacion2 = (variacion2 > 100) ? "incremento " + Math.round(variacion2 - 100) + "%" :
                (variacion2 < 100) ? "disminución " + Math.round(100 - variacion2) + "%" :
                        "sin variación";

// Cálculos: Deuda total en el sector bancario
        double deudaTotalBCRA1 = postBalance1.getTotalDeudaSegunBCRA();
        double deudaTotalBCRA2 = postBalance2.getTotalDeudaSegunBCRA();

// Cálculos: Días de ventas postbalance
        double diasVentasPost1 = (promedioVentasBalance1 != 0) ? (deudaTotalBCRA1 / promedioVentasBalance1) * 30 : 0;
        double diasVentasPost2 = (promedioVentasBalance2 != 0) ? (deudaTotalBCRA2 / promedioVentasBalance2) * 30 : 0;

// Agregar filas
        addRow(ventasDeudaTable, "Promedio de ventas mensuales postbalance", postBalance1.getPromedioVentasMensuales(), postBalance2.getPromedioVentasMensuales());
        addRow(ventasDeudaTable, "Variación respecto último Balance", textoVariacion1, textoVariacion2);
        addRow(ventasDeudaTable, "Deuda Total en el Sector Bancario", deudaTotalBCRA1, deudaTotalBCRA2);
        addRow(ventasDeudaTable, "Días de ventas postbalance", diasVentasPost1, diasVentasPost2);

        document.add(ventasDeudaTable);

        ReglaAnalisisService analisisService = new ReglaAnalisisService();
        ResultadoAnalisis resultado = analisisService.analizar(empresa, balance1, postBalance1);


        NumberFormat formato = NumberFormat.getNumberInstance(new Locale("es", "AR"));
        String montoFormateado = formato.format(resultado.lineaOtorgada);

        document.add(new Paragraph("\nLíneas de créditos a otorgar").setBold().setFontSize(14));
        document.add(new Paragraph("Linea Otorgada: " + montoFormateado));
        document.add(new Paragraph("Puntaje Total: " + resultado.puntajeTotal));
        document.add(new Paragraph("Comentario: " + resultado.comentario));


        document.close();
    }

    private void addRow(Table table, String label, double value1, double value2) {
        table.addCell(label);
        table.addCell(String.format(Locale.US, "%,.2f", value1));
        table.addCell(String.format(Locale.US, "%,.2f", value2));
    }

    private void addRow(Table table, String label, String value1, String value2) {
        table.addCell(new Cell().add(new Paragraph(label)));
        table.addCell(new Cell().add(new Paragraph(value1)));
        table.addCell(new Cell().add(new Paragraph(value2)));
    }

    private void addHeaderRow(Table table, String year1, String year2) {
        table.addCell("");
        table.addCell(year1);
        table.addCell(year2);
    }

    private double safeDivide(double numerator, double denominator) {
        return denominator != 0 ? numerator / denominator : 0;
    }

}