package com.qip.engine;

import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.PostBalance;

public class ReglaAnalisisService {

    public ResultadoAnalisis analizar(Empresa empresa, DatosDeBalance datosDeBalance, PostBalance postBalance) {
        int puntajeTotal = 0;

        // 1. Categoría de industria
        int categoriaIndustria = Integer.parseInt(empresa.getIndustria().getCategoria());
        puntajeTotal += evaluarCategoriaIndustria(categoriaIndustria);

        // 2. Score Nosis
        int score = empresa.getScoreNosis();
        puntajeTotal += evaluarScoreNosis(score);

        // 3. NFD / EBITDA
        double nfd = (datosDeBalance.getDeudaBancariaTotal() + datosDeBalance.getDeudaFinancieraTotal()) - datosDeBalance.getCajaEInversionesCorrientes();
        double ebitda = datosDeBalance.getResultadoOperativo() + datosDeBalance.getAmortizacionesYDepreciaciones();
        puntajeTotal += evaluarNFDSobreEBITDA(nfd, ebitda);

        // 4. Cobertura de intereses
        double intereses = datosDeBalance.getInteresesPagados();
        puntajeTotal += evaluarCoberturaIntereses(ebitda, intereses);

        // 5. Margen EBITDA
        double ventas = datosDeBalance.getVentasNetas();
        puntajeTotal += evaluarMargenEBITDA(ebitda, ventas);

        // 6. Cantidad de bancos
        int bancos = postBalance.getCantidadBancos();
        puntajeTotal += bancos < 10 ? 1 : 0;

        // 7. Situación BCRA
        int situacionBCRA = Integer.parseInt(postBalance.getSituacionBCRA());
        if (situacionBCRA == 1) {
            puntajeTotal += 1;
        } else {
            return ResultadoAnalisis.noOtorgarLinea("Situación BCRA desfavorable");
        }

        // 8. Cheques rechazados
        if (postBalance.getChequesRechazados()>0) {
            return ResultadoAnalisis.noOtorgarLinea("Cheques rechazados");
        } else {
            puntajeTotal += 1;
        }

        // Decisión final
        double promedioVentasMensuales = postBalance.getPromedioVentasMensuales();
        double lineaOtorgada;
        String comentario;

        if (puntajeTotal >= 10) {
            lineaOtorgada = promedioVentasMensuales * 3;
            comentario = "Se otorgan 3 veces promedio ventas";
        } else if (puntajeTotal >= 7) {
            lineaOtorgada = promedioVentasMensuales * 2;
            comentario = "Se otorgan 2 veces promedio ventas";
        } else if (puntajeTotal >= 5) {
            lineaOtorgada = promedioVentasMensuales;
            comentario = "Se otorga 1 vez promedio ventas";
        } else {
            lineaOtorgada = promedioVentasMensuales;
            comentario = "Se otorga 1 vez promedio ventas con fianza de accionista";
        }

        return new ResultadoAnalisis(puntajeTotal, lineaOtorgada, comentario);
    }

    // Métodos auxiliares para evaluar criterios (ejemplos):
    private int evaluarCategoriaIndustria(int categoria) {
        return switch (categoria) {
            case 1 -> 2;
            case 2 -> 1;
            case 3 -> 0;
            default -> 0;
        };
    }

    private int evaluarScoreNosis(int score) {
        if (score > 650) return 2;
        if (score >= 500) return 1;
        return 0;
    }

    private int evaluarNFDSobreEBITDA(double nfd, double ebitda) {
        double ratio = ebitda == 0 ? Double.MAX_VALUE : nfd / ebitda;
        if (ratio > 4) return 0;
        if (ratio >= 1.5) return 1;
        return 2;
    }

    private int evaluarCoberturaIntereses(double ebitda, double intereses) {
        return (intereses / ebitda) > 1 ? 0 : 1;
    }

    private int evaluarMargenEBITDA(double ebitda, double ventas) {
        double margen = ventas == 0 ? 0 : ebitda / ventas ;
        if (margen > 0.025) return 2;
        if (margen >= 0.01) return 1;
        return 0;
    }

    private double calcularEBITDA(DatosDeBalance db) {
        return db.getResultadoOperativo() + db.getAmortizacionesYDepreciaciones();
    }
}
