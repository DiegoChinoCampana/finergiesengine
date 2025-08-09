package com.qip.engine;

import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.PostBalance;

import java.util.ArrayList;
import java.util.List;

public class ReglaAnalisisService {



    public ResultadoAnalisis analizar(Empresa empresa, DatosDeBalance datos, PostBalance post) {
        int puntaje = 0;
        List<String> pasos = new ArrayList<>();

        pasos.add("Análisis empresa: " + empresa.getNombre());

        // Categoría industria
        int categoriaPuntos = evaluarCategoriaIndustria(Integer.parseInt(empresa.getIndustria().getCategoria()));
        puntaje += categoriaPuntos;
        pasos.add("Categoría industria (" + empresa.getIndustria().getCategoria() + "): +" + categoriaPuntos + " puntos. Puntaje acumulado: " + puntaje);

        // Score Nosis
        int score = empresa.getScoreNosis();
        int scorePuntos = evaluarScoreNosis(score);
        puntaje += scorePuntos;
        pasos.add("Score Nosis (" + score + "): +" + scorePuntos + " puntos. Puntaje acumulado: " + puntaje);

        // Ratios financieros
        double nfd = datos.getDeudaFinancieraPasivoCorriente() + datos.getDeudaFinancieraPasivoNoCorriente()
                + datos.getDeudaBancariaPasivoCorriente() + datos.getDeudaBancariaPasivoNoCorriente()
                - datos.getCajaEInversionesCorrientes();

        double ebitda = datos.getResultadoOperativo() + datos.getAmortizacionesYDepreciaciones();

        pasos.add("NFD calculado: " + nfd);
        pasos.add("EBITDA calculado: " + ebitda);

        if (ebitda >= 0) {
            int nfdEbitdaPuntos = evaluarNFDSobreEBITDA(nfd, ebitda);
            puntaje += nfdEbitdaPuntos;
            pasos.add("Evaluación NFD/EBITDA: +" + nfdEbitdaPuntos + " puntos. Puntaje acumulado: " + puntaje);
        } else {
            pasos.add("EBITDA negativo, no se suma puntaje NFD/EBITDA.");
        }

        if (nfd < 0) {
            puntaje += 2;
            pasos.add("NFD negativo, +2 puntos extra. Puntaje acumulado: " + puntaje);
        }

        // Cobertura intereses
        int coberturaInteresesPuntos = evaluarCoberturaIntereses(ebitda, datos.getInteresesPagados());
        puntaje += coberturaInteresesPuntos;
        pasos.add("Cobertura intereses: +" + coberturaInteresesPuntos + " puntos. Puntaje acumulado: " + puntaje);

        // Margen EBITDA
        int margenEbitdaPuntos = evaluarMargenEBITDA(ebitda, datos.getVentasNetas());
        puntaje += margenEbitdaPuntos;
        pasos.add("Margen EBITDA: +" + margenEbitdaPuntos + " puntos. Puntaje acumulado: " + puntaje);


        // Bancos
        int bancosPuntos = post.getCantidadBancos() < 10 ? 1 : 0;
        puntaje += bancosPuntos;
        pasos.add("Cantidad bancos ("+post.getCantidadBancos()+": +" + bancosPuntos + " puntos. Puntaje acumulado: " + puntaje);


        // Situación BCRA
        if (!"1".equals(post.getSituacionBCRA())) {
            pasos.add("Situación BCRA desfavorable, análisis finalizado.");
            return ResultadoAnalisis.noOtorgarLinea("Situación BCRA desfavorable", pasos);
        }
        puntaje += 1;
        pasos.add("Situación BCRA favorable: +1 punto. Puntaje acumulado: " + puntaje);

        // Cheques rechazados
        if (post.getChequesRechazados() > 0) {
            pasos.add("Cheques rechazados encontrados, análisis finalizado.");
            return ResultadoAnalisis.noOtorgarLinea("Cheques rechazados", pasos);
        }
        puntaje += 1;
        pasos.add("Sin cheques rechazados: +1 punto. Puntaje acumulado: " + puntaje);

        // Línea crédito
        double promedioVentas = post.getPromedioVentasMensuales();
        pasos.add("Promedio ventas mensuales: " + promedioVentas);

        double linea;
        String comentario;
        boolean requiereGarantia;

        if (puntaje >= 10 && puntaje <= 12) {
            linea = promedioVentas * 1.5;
            comentario = "Se otorgan 1,5 veces promedio ventas postbalance";
            requiereGarantia = false;
        } else if (puntaje >= 7) {
            linea = promedioVentas;
            comentario = "Se otorgan 1 vez promedio ventas postbalance";
            requiereGarantia = false;
        } else if (puntaje >= 5) {
            linea = promedioVentas * 0.5;
            comentario = "Se otorgan 0,5 veces promedio ventas postbalance";
            requiereGarantia = false;
        } else {
            linea = promedioVentas * 0.5;
            comentario = "Se otorgan 0,5 veces promedio ventas postbalance con garantía a satisfacción";
            requiereGarantia = true;
        }

        pasos.add("Línea crédito final: " + linea + ", " + comentario + (requiereGarantia ? " (requiere garantía)" : ""));

        return new ResultadoAnalisis(puntaje, linea, comentario, requiereGarantia, pasos);

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
        return (ebitda / intereses) > 1 ? 0 : 1;
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
