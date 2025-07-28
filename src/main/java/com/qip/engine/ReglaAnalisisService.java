package com.qip.engine;

import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.PostBalance;

public class ReglaAnalisisService {

    public ResultadoAnalisis analizar(Empresa empresa, DatosDeBalance datos, PostBalance post) {
        int puntaje = 0;
        System.out.println("Analizando empresa: " + empresa.getNombre());

        // Categoría industria
        puntaje += evaluarCategoriaIndustria(Integer.parseInt(empresa.getIndustria().getCategoria()));
        System.out.println("Categoría industria: " + empresa.getIndustria().getCategoria() + ", Puntaje: " + puntaje);
        // Score
        int score = empresa.getScoreNosis();
        System.out.println("Score Nosis: " + score);
        puntaje += evaluarScoreNosis(score);
        System.out.println("Score Nosis evaluado, Puntaje: " + puntaje);

        // Ratios financieros
        double nfd = datos.getDeudaFinancieraPasivoCorriente() + datos.getDeudaFinancieraPasivoNoCorriente()
                + datos.getDeudaBancariaPasivoCorriente() + datos.getDeudaBancariaPasivoNoCorriente()
                - datos.getCajaEInversionesCorrientes();
        System.out.println("NFD calculado: " + nfd);
        double ebitda = datos.getResultadoOperativo() + datos.getAmortizacionesYDepreciaciones();
        System.out.println("EBITDA calculado: " + ebitda);
        puntaje += evaluarNFDSobreEBITDA(nfd, ebitda);
        System.out.println("NFD sobre EBITDA evaluado, Puntaje: " + puntaje);
        puntaje += evaluarCoberturaIntereses(ebitda, datos.getInteresesPagados());
        System.out.println("Cobertura de intereses evaluada, Puntaje: " + puntaje);
        puntaje += evaluarMargenEBITDA(ebitda, datos.getVentasNetas());
        System.out.println("Margen EBITDA evaluado, Puntaje: " + puntaje);

        // Bancos
        puntaje += post.getCantidadBancos() < 10 ? 1 : 0;
        System.out.println("Cantidad de bancos evaluada, Puntaje: " + puntaje);
        // BCRA
        if (!"1".equals(post.getSituacionBCRA())) {
            return ResultadoAnalisis.noOtorgarLinea("Situación BCRA desfavorable");
        }
        puntaje += 1;
        System.out.println("Situación BCRA evaluada, Puntaje: " + puntaje);
        // Cheques rechazados
        if (post.getChequesRechazados() > 0) {
            return ResultadoAnalisis.noOtorgarLinea("Cheques rechazados");
        }
        puntaje += 1;
        System.out.println("Cheques rechazados evaluados, Puntaje: " + puntaje);
        // Línea a otorgar y garantía basada SOLO en puntaje total
        double promedioVentas = post.getPromedioVentasMensuales();
        System.out.println("Promedio de ventas mensuales: " + promedioVentas);

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

        return new ResultadoAnalisis(puntaje, linea, comentario, requiereGarantia);

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
