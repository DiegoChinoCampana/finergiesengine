package com.qip.engine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.PostBalance;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlFinancialReportService {

    public void generateFromHtmlTemplate(String htmlTemplatePath,
                                         String stylesTemplatePath,
                                         Empresa empresa,
                                         DatosDeBalance balanceActual,
                                         DatosDeBalance balanceAnterior,
                                         PostBalance postBalanceActual,
                                         PostBalance postBalanceAnterior,
                                         String outputPath) throws Exception {

        // Leer template HTML
        String html = loadHtmlTemplate(htmlTemplatePath);
        String css = loadHtmlTemplate(stylesTemplatePath);
        String base64 = encodeImageFromResource("templates/Finergies-Logo.png");
        String base64Inter = encodeImageFromResource("templates/Finergies-LogoInter.png");
        Map<String, String> imagenes = new HashMap<>();
        imagenes.put("main-logo", base64);
        imagenes.put("intern-logo", base64Inter);
        html = reemplazarTodosLosLogosSvgPorImagen(html, imagenes);


// ahora tenés el HTML listo para renderizar como PDF



        html = html.replace("<link rel=\"stylesheet\" href=\"styles.css\"/>", "<style>\n" + css + "\n</style>");


        // Reemplazar placeholders
        html = html.replace("${empresa_nombre}", empresa.getNombre());
        html = html.replace("${cuit}", empresa.getCuit());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateadaActual = balanceActual.getEjercicio().format(formatter);
        String fechaFormateadaAnterior = balanceAnterior.getEjercicio().format(formatter);
        html = html.replace("${fecha ejercicio actual}", fechaFormateadaActual);
        html = html.replace("${ejercicio anterior}", fechaFormateadaAnterior);
        html = html.replace("${ejercicio actual}", fechaFormateadaActual);


        DecimalFormat nf = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("es", "AR"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(true);
        nf.setNegativePrefix("-");
        nf.setNegativeSuffix("");

        String valor = formatDecimal(1234567.00, nf); // → "1.234.567"
        String valor2 = formatDecimal(1234567.89, nf); // → "1.234.567,89"

        html = html.replace("${ventas netas anterior}", formatDecimal(balanceAnterior.getVentasNetas(),nf));
        html = html.replace("${ventas netas actual}", formatDecimal(balanceActual.getVentasNetas(),nf));

        double resultadoBrutoAnterior = balanceAnterior.getVentasNetas() - balanceAnterior.getCostoMercaderiasVendidas();
        double resultadoBrutoActual = balanceActual.getVentasNetas() - balanceActual.getCostoMercaderiasVendidas();

        html = html.replace("${resultado bruto anterior}", formatDecimal(resultadoBrutoAnterior,nf));
        html = html.replace("${resultado bruto actual}", formatDecimal(resultadoBrutoActual,nf));

        double margenBrutoAnterior = balanceAnterior.getVentasNetas() != 0
                ? resultadoBrutoAnterior / balanceAnterior.getVentasNetas() * 100
                : 0.0;

        double margenBrutoActual = balanceActual.getVentasNetas() != 0
                ? resultadoBrutoActual / balanceActual.getVentasNetas() * 100
                : 0.0;

        html = html.replace("${margen bruto anterior}", formatDecimal(margenBrutoAnterior,nf));
        html = html.replace("${margen bruto actual}", formatDecimal(margenBrutoActual,nf));


        html = html.replace("${resultado operativo anterior}", formatDecimal(balanceAnterior.getResultadoOperativo(),nf))  ;
        html = html.replace("${resultado operativo actual}", formatDecimal(balanceActual.getResultadoOperativo(),nf));

        html = html.replace("${margen operativo anterior}", formatDecimal((balanceAnterior.getResultadoOperativo() / balanceAnterior.getVentasNetas()) * 100,nf))  ;
        html = html.replace("${margen operativo actual}", formatDecimal((balanceActual.getResultadoOperativo() / balanceActual.getVentasNetas()) * 100,nf));


        double ebitda1 = balanceAnterior.getResultadoOperativo() + balanceAnterior.getAmortizacionesYDepreciaciones();
        System.out.println("balanceAnterior.getResultadoOperativo() " + balanceAnterior.getResultadoOperativo());
        System.out.println("balanceAnterior.getAmortizacionesYDepreciaciones() " + balanceAnterior.getAmortizacionesYDepreciaciones());
        System.out.println("ebitda1 " + ebitda1);

        double ebitda2 = balanceActual.getResultadoOperativo() + balanceActual.getAmortizacionesYDepreciaciones();
        System.out.println("balanceActual.getResultadoOperativo() " + balanceActual.getResultadoOperativo());
        System.out.println("balanceActual.getAmortizacionesYDepreciaciones() " + balanceActual.getAmortizacionesYDepreciaciones());
        System.out.println("ebitda2 " + ebitda2);

        html = html.replace("${ebitda anterior}", formatDecimal(ebitda1,nf))  ;
        html = html.replace("${ebitda actual}", formatDecimal(ebitda2,nf));

        html = html.replace("${margen ebitda anterior}", formatDecimal((ebitda1 / balanceAnterior.getVentasNetas()) * 100,nf))  ;
        System.out.println("balanceAnterior.getVentasNetas() " + balanceAnterior.getVentasNetas());
        System.out.println("ebitda1 / balanceAnterior.getVentasNetas() " + (ebitda1 / balanceAnterior.getVentasNetas()));
        System.out.println("margen ebitda anterior " + (ebitda1 / balanceAnterior.getVentasNetas()) * 100);

        html = html.replace("${margen ebitda actual}", formatDecimal((ebitda2 / balanceActual.getVentasNetas()) * 100,nf))  ;
        System.out.println("balanceActual.getVentasNetas() " + balanceActual.getVentasNetas());
        System.out.println("ebitda2 / balanceActual.getVentasNetas() " + (ebitda2 / balanceActual.getVentasNetas()));
        System.out.println("margen ebitda actual " + (ebitda2 / balanceActual.getVentasNetas()) * 100);

        html = html.replace("${resultado neto anterior}", formatDecimal(balanceAnterior.getResultadoNeto(),nf))  ;
        html = html.replace("${resultado neto actual}", formatDecimal(balanceActual.getResultadoNeto(),nf))  ;

        html = html.replace("${margen neto anterior}", formatDecimal((balanceAnterior.getResultadoNeto() / balanceAnterior.getVentasNetas()) * 100,nf)) ;
        html = html.replace("${margen neto actual}", formatDecimal((balanceActual.getResultadoNeto() / balanceActual.getVentasNetas()) * 100,nf)) ;

        html = html.replace("${activos corrientes anterior}", formatDecimal(balanceAnterior.getActivoCorriente(),nf)) ;
        html = html.replace("${activos corrientes actual}", formatDecimal(balanceActual.getActivoCorriente(),nf)) ;

        html = html.replace("${activos no corrientes anterior}", formatDecimal(balanceAnterior.getActivoNoCorriente(),nf)) ;
        html = html.replace("${activos no corrientes actual}", formatDecimal(balanceActual.getActivoNoCorriente(),nf)) ;

        double totalActivo1 = balanceAnterior.getActivoCorriente() + balanceAnterior.getActivoNoCorriente();
        double totalActivo2 = balanceActual.getActivoCorriente() + balanceActual.getActivoNoCorriente();

        html = html.replace("${total de activos anterior}", formatDecimal(totalActivo1,nf)) ;
        html = html.replace("${total de activos actual}", formatDecimal(totalActivo2,nf)) ;

        html = html.replace("${pasivos corrientes anterior}", formatDecimal(balanceAnterior.getPasivoCorriente() ,nf)) ;
        html = html.replace("${pasivos corrientes actual}", formatDecimal(balanceActual.getPasivoCorriente() ,nf)) ;

        html = html.replace("${pasivos no corrientes anterior}", formatDecimal(balanceAnterior.getPasivoNoCorriente() ,nf)) ;
        System.out.println("balanceActual.getPasivoNoCorriente() " + balanceActual.getPasivoNoCorriente());
        html = html.replace("${pasivos no corrientes actual}", formatDecimal(balanceActual.getPasivoNoCorriente() ,nf)) ;

        double totalPasivo1 = balanceAnterior.getPasivoCorriente() + balanceAnterior.getPasivoNoCorriente();
        double totalPasivo2 = balanceActual.getPasivoCorriente() + balanceActual.getPasivoNoCorriente();

        html = html.replace("${total del pasivo anterior}", formatDecimal(totalPasivo1,nf)) ;
        html = html.replace("${total del pasivo actual}", formatDecimal(totalPasivo2 ,nf)) ;

        double patrimonioNetoAnterior = totalActivo1 - totalPasivo1;
        double patrimonioNetoActual = totalActivo2 - totalPasivo2;

        html = html.replace("${patrimonio neto anterior}", formatDecimal(patrimonioNetoAnterior,nf));
        html = html.replace("${patrimonio neto actual}", formatDecimal(patrimonioNetoActual,nf));


        String periodoCobroAnterior = (balanceAnterior.getCreditosPorVentas() == 0)
                ? "No Aplica"
                : formatDecimal((balanceAnterior.getCreditosPorVentas() / balanceAnterior.getVentasNetas()) * 365,nf);

        String periodoCobroActual = (balanceActual.getCreditosPorVentas() == 0)
                ? "No Aplica"
                : formatDecimal((balanceActual.getCreditosPorVentas() / balanceActual.getVentasNetas()) * 365,nf);

        html = html.replace("${periodo de cobro anterior}", periodoCobroAnterior);
        html = html.replace("${periodo de cobro actual}", periodoCobroActual);

        String periodoPagosAnterior = (balanceAnterior.getProveedores() == 0 || balanceAnterior.getCompras() == 0)
                ? "No Aplica"
                : formatDecimal((balanceAnterior.getProveedores() / balanceAnterior.getCompras()) * 365,nf);

        String periodoPagosActual = (balanceActual.getProveedores() == 0 || balanceActual.getCompras() == 0)
                ? "No Aplica"
                : formatDecimal((balanceActual.getProveedores() / balanceActual.getCompras()) * 365,nf);

        html = html.replace("${periodo de pagos anterior}", periodoPagosAnterior);
        html = html.replace("${periodo de pagos actual}", periodoPagosActual);

        String rotacionInventarioAnterior = (balanceAnterior.getMercaderias() == 0)
                ? "No Aplica"
                : formatDecimal((balanceAnterior.getMercaderias() / balanceAnterior.getCostoMercaderiasVendidas()) * 365,nf);

        String rotacionInventarioActual = (balanceActual.getMercaderias() == 0)
                ? "No Aplica"
                : formatDecimal((balanceActual.getMercaderias() / balanceActual.getCostoMercaderiasVendidas()) * 365,nf);

        html = html.replace("${rotacion de inventarios anterior}", rotacionInventarioAnterior);
        html = html.replace("${rotacion de inventarios actual}", rotacionInventarioActual);

        double nct1 = balanceAnterior.getCreditosPorVentas() + balanceAnterior.getMercaderias() - balanceAnterior.getProveedores();
        double nct2 = balanceActual.getCreditosPorVentas() + balanceActual.getMercaderias() - balanceActual.getProveedores();
        double gap1 = nct1 - balanceAnterior.getDeudaBancariaPasivoCorriente() - balanceAnterior.getDeudaFinancieraPasivoCorriente();
        double gap2 = nct2 - balanceActual.getDeudaBancariaPasivoCorriente() - balanceActual.getDeudaFinancieraPasivoCorriente();

        html = html.replace("${gap anterior}", formatDecimal(gap1,nf)) ;
        html = html.replace("${gap actual}", formatDecimal(gap2,nf)) ;

        html = html.replace("${necesidad de capital de trabajo anterior}", formatDecimal(nct1,nf)) ;
        html = html.replace("${necesidad de capital de trabajo actual}", formatDecimal(nct2,nf)) ;


        double nfd1 = balanceAnterior.getDeudaFinancieraPasivoCorriente() + balanceAnterior.getDeudaFinancieraPasivoNoCorriente() + balanceAnterior.getDeudaBancariaPasivoCorriente() + balanceAnterior.getDeudaBancariaPasivoNoCorriente() - balanceAnterior.getCajaEInversionesCorrientes();
        double nfd2 = balanceActual.getDeudaFinancieraPasivoCorriente() + balanceActual.getDeudaFinancieraPasivoNoCorriente() + balanceActual.getDeudaBancariaPasivoCorriente() + balanceActual.getDeudaBancariaPasivoNoCorriente() - balanceActual.getCajaEInversionesCorrientes();
        double tds1 = balanceAnterior.getDeudaBancariaPasivoCorriente() + balanceAnterior.getDeudaFinancieraPasivoCorriente() + balanceAnterior.getInteresesPagados();
        double tds2 = balanceActual.getDeudaBancariaPasivoCorriente() + balanceActual.getDeudaFinancieraPasivoCorriente() + balanceActual.getInteresesPagados();

        double deudaFinancieraAnterior = nfd1 + balanceAnterior.getCajaEInversionesCorrientes();
        double deudaFinancieraActual = nfd2 + balanceActual.getCajaEInversionesCorrientes();

        html = html.replace("${caja e inversiones corrientes anterior}", formatDecimal(balanceAnterior.getCajaEInversionesCorrientes(),nf)) ;
        html = html.replace("${caja e inversiones corrientes actual}", formatDecimal(balanceActual.getCajaEInversionesCorrientes(),nf)) ;

        html = html.replace("${deuda financiera anterior}", formatDecimal(deudaFinancieraAnterior,nf)) ;
        html = html.replace("${deuda financiera actual}", formatDecimal(deudaFinancieraActual,nf)) ;

        html = html.replace("${nfd anterior}", formatDecimal(nfd1,nf)) ;
        html = html.replace("${nfd actual}", formatDecimal(nfd2,nf)) ;

        html = html.replace("${tds anterior}", formatDecimal(tds1,nf)) ;
        html = html.replace("${tds actual}", formatDecimal(tds2,nf)) ;

        String coberturaInteresesAnterior = (balanceAnterior.getInteresesPagados() == 0 || ebitda1 <= 0)
                ? "No Aplica"
                : formatDecimal( ebitda1 / balanceAnterior.getInteresesPagados(),nf);

        String coberturaInteresesActual = (balanceActual.getInteresesPagados() == 0 || ebitda2 <= 0)
                ? "No Aplica"
                : formatDecimal(ebitda2 / balanceActual.getInteresesPagados(),nf);

        html = html.replace("${cobertura de intereses anterior}", coberturaInteresesAnterior);
        html = html.replace("${cobertura de intereses actual}", coberturaInteresesActual);


        html = html.replace("${ratio de liquidez anterior}", formatDecimal(balanceAnterior.getActivoCorriente() / balanceAnterior.getPasivoCorriente(),nf)) ;
        html = html.replace("${ratio de liquidez actual}", formatDecimal(balanceActual.getActivoCorriente() / balanceActual.getPasivoCorriente(),nf)) ;

        String nfdEbitdaAnterior = (nfd1 <= 0 || ebitda1 <= 0)
                ? "No Aplica"
                : formatDecimal(nfd1 / ebitda1,nf);

        String nfdEbitdaActual = (nfd2 <= 0 || ebitda2 <= 0)
                ? "No Aplica"
                : formatDecimal(nfd2 / ebitda2,nf);

        html = html.replace("${nfd ebitda anterior}", nfdEbitdaAnterior);
        html = html.replace("${nfd ebitda actual}", nfdEbitdaActual);


        html = html.replace("${tds ebitda anterior}", formatDecimal(tds1 / ebitda1,nf)) ;
        html = html.replace("${tds ebitda actual}", formatDecimal(tds2 / ebitda2,nf)) ;


// Cálculos: Promedio de ventas mensuales postbalance
        double promedioVentasBalanceAnterior = balanceAnterior.getVentasNetas() / 12;
        double promedioVentasBalanceActual = balanceActual.getVentasNetas() / 12;


// Cálculos: Variación respecto al último balance (%)
        double variacion1 = (postBalanceAnterior.getPromedioVentasMensuales() != 0) ? (postBalanceAnterior.getPromedioVentasMensuales() / promedioVentasBalanceAnterior) * 100 : 0;
        double variacion2 = (postBalanceActual.getPromedioVentasMensuales() != 0) ? (postBalanceActual.getPromedioVentasMensuales() / promedioVentasBalanceActual) * 100 : 0;

// Texto de variación (ej: "incremento 39%")
        String textoVariacion1 = (variacion1 > 100) ? "+ " + Math.round(variacion1 - 100) + "%" :
                (variacion1 < 100) ? "- " + Math.round(100 - variacion1) + "%" :
                        "sin variación";
        String textoVariacion2 = (variacion2 > 100) ? "+ " + Math.round(variacion2 - 100) + "%" :
                (variacion2 < 100) ? "- " + Math.round(100 - variacion2) + "%" :
                        "sin variación";

// Cálculos: Deuda total en el sector bancario
        double deudaTotalBCRA1 = postBalanceAnterior.getTotalDeudaSegunBCRA();
        double deudaTotalBCRA2 = postBalanceActual.getTotalDeudaSegunBCRA();

// Cálculos: Días de ventas postbalance
        double diasVentasPost1 = (promedioVentasBalanceAnterior != 0) ? (deudaTotalBCRA1 / postBalanceAnterior.getPromedioVentasMensuales()) * 30 : 0;
        double diasVentasPost2 = (promedioVentasBalanceActual != 0) ? (deudaTotalBCRA2 / postBalanceActual.getPromedioVentasMensuales()) * 30 : 0;

        html = html.replace("${promedio de ventas mensuales anterior}", formatDecimal(postBalanceAnterior.getPromedioVentasMensuales(),nf)) ;
        html = html.replace("${promedio de ventas mensuales actual}", formatDecimal(postBalanceActual.getPromedioVentasMensuales(),nf)) ;

        html = html.replace("${variacion ultimo balance anterior}", textoVariacion1) ;
        html = html.replace("${variacion ultimo balance actual}", textoVariacion2) ;

        html = html.replace("${deuda total bancario anterior}", formatDecimal(deudaTotalBCRA1,nf)) ;
        html = html.replace("${deuda total bancario actual}", formatDecimal(deudaTotalBCRA2,nf)) ;

        html = html.replace("${deuda ventas anterior}", formatDecimal(diasVentasPost1,nf)) ;
        html = html.replace("${deuda ventas actual}", formatDecimal(diasVentasPost2,nf)) ;

        ReglaAnalisisService analisisService = new ReglaAnalisisService();
        ResultadoAnalisis resultado = analisisService.analizar(empresa, balanceActual, postBalanceActual);


        NumberFormat formato = NumberFormat.getNumberInstance(new Locale("es", "AR"));
        String montoFormateado = formato.format(resultado.lineaOtorgada);

        String leyenda;
        if (resultado.lineaOtorgada == 0) {
            leyenda = resultado.comentario;
        } else {
            leyenda = "Línea de crédito sugerida: Pesos " + montoFormateado + ".<br/>"
                    + (resultado.requiereGarantia
                    ? "Requiere garantía a satisfacción."
                    : "No requiere garantía.");
        }
        html = html.replace("${linea credito sugerida}", leyenda);
        System.out.println(html);

        // Construcción segura con CDATA
        StringBuilder detallePasos = new StringBuilder();
        detallePasos.append("<ul>");
        for(String paso : resultado.pasos){
            detallePasos.append("<li><![CDATA[").append(paso).append("]]></li>");
        }
        detallePasos.append("</ul>");

        html = html.replace("${detalle pasos analisis}", detallePasos.toString());

        // Convertir a PDF
        generatePdfFromHtml(html, outputPath);
    }

    private String loadHtmlTemplate(String path) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("No se encontró el archivo de plantilla: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void generatePdfFromHtml(String html, String outputPath) throws IOException {
        try (OutputStream os = new FileOutputStream(outputPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.useFastMode();
            builder.toStream(os);
            builder.run();
        }
    }

    private double safeDivide(double numerator, double denominator) {
        return denominator != 0 ? numerator / denominator : 0;
    }

    private String formatDecimal(Double valor, DecimalFormat nf) {
        if (valor == null) return "0";

        String formateado = nf.format(valor); // ✅ Acá estaba el error

        // Si termina en ",00", eliminamos esa parte
        if (formateado.endsWith(",00")) {
            return formateado.substring(0, formateado.length() - 3);
        }

        return formateado;
    }

    private String encodeImageFromResource(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("No se encontró la imagen: " + path);
            }
            byte[] imageBytes = is.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la imagen desde recursos", e);
        }
    }

    public String reemplazarTodosLosLogosSvgPorImagen(String html, Map<String, String> imagenesBase64PorClase) {
        Pattern pattern = Pattern.compile(
                "(?s)<figure class=\"(main-logo|intern-logo)\">\\s*<svg[^>]*?width=\"(\\d+)\"\\s+height=\"(\\d+)\"[^>]*>.*?</svg>\\s*</figure>");

        Matcher matcher = pattern.matcher(html);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String clase = matcher.group(1);
            String width = matcher.group(2);
            String height = matcher.group(3);

            String base64Image = imagenesBase64PorClase.get(clase);
            if (base64Image == null) continue;

            String figuraReemplazo = "<figure class=\"" + clase + "\">" +
                    "<img src=\"data:image/png;base64," + base64Image + "\" width=\"" + width + "\" height=\"" + height + "\" />" +
                    "</figure>";

            matcher.appendReplacement(sb, Matcher.quoteReplacement(figuraReemplazo));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }




}