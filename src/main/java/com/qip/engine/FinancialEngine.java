package com.qip.engine;

import com.qip.jpa.entities.DatosDeBalance;

public class FinancialEngine {

    public static double calcularMargenBruto(double resultadoBruto, double ventasNetas) {
        return (ventasNetas != 0) ? (resultadoBruto / ventasNetas) * 100 : 0;
    }

    public static double calcularMargenOperativo(double resultadoOperativo, double ventasNetas) {
        return (ventasNetas != 0) ? (resultadoOperativo / ventasNetas) * 100 : 0;
    }

    public static double calcularEBITDA(double resultadoOperativo, double amortizacionesYDepreciaciones) {
        return resultadoOperativo + amortizacionesYDepreciaciones;
    }

    public static double calcularMargenEBITDA(double ebitda, double ventasNetas) {
        return (ventasNetas != 0) ? (ebitda / ventasNetas) * 100 : 0;
    }

    public static double calcularMargenNeto(double resultadoNeto, double ventasNetas) {
        return (ventasNetas != 0) ? (resultadoNeto / ventasNetas) * 100 : 0;
    }

    public static double calcularNFD(double deudaBancariaPasivoCorriente,double deudaBancariaPasivoNoCorriente, double deudaFinancieraPasivoCorriente,double deudaFinancieraPasivoNoCorriente, double cajaEInversiones) {
        return deudaBancariaPasivoCorriente + deudaBancariaPasivoNoCorriente + deudaFinancieraPasivoCorriente + deudaFinancieraPasivoNoCorriente - cajaEInversiones;
    }

    public static double calcularTDS(double deudaBancariaCP, double deudaFinancieraCP, double interesesPagados) {
        return deudaBancariaCP + deudaFinancieraCP + interesesPagados;
    }

    public static double calcularLiquidez(double activoCorriente, double pasivoCorriente) {
        return (pasivoCorriente != 0) ? activoCorriente / pasivoCorriente : 0;
    }

    public static double calcularCoberturaIntereses(double interesesPagados, double ebitda) {
        return (ebitda != 0) ? interesesPagados / ebitda : 0;
    }

    public static double calcularDiasCobro(double creditosPorVentas, double ventasNetas) {
        return (ventasNetas != 0) ? (creditosPorVentas / ventasNetas) * 365 : 0;
    }

    public static double calcularDiasPago(double proveedores, double compras) {
        return (compras != 0) ? (proveedores / compras) * 365 : 0;
    }

    public static double calcularRotacionInventarios(double mercaderias, double costoMercaderiasVendidas) {
        return (costoMercaderiasVendidas != 0) ? (mercaderias / costoMercaderiasVendidas) * 365 : 0;
    }

    public static double calcularPromedioVentasMensuales(double ventasAnuales) {
        return ventasAnuales / 12;
    }

    public static double calcularRelacionDeudaVentasDias(double deudaBCRA, double promedioVentasMensuales) {
        return (promedioVentasMensuales != 0) ? (deudaBCRA / promedioVentasMensuales) / 30 : 0;
    }

    public static double calcularVariacionVentasMensuales(double promedioVentasPost, double promedioVentasBalance) {
        return (promedioVentasBalance != 0) ? (promedioVentasPost / promedioVentasBalance) * 100 : 0;
    }

    public static double calcularNCT(double creditosPorVentas, double mercaderias, double proveedores) {
        return creditosPorVentas + mercaderias - proveedores;
    }

    public static double calcularGAP(double nct, double deudaBancariaCP) {
        return nct - deudaBancariaCP;
    }

    public static double calcularMontoLineasCredito(double promedioVentasMensuales) {
        return promedioVentasMensuales * 3;
    }

    public static double calcularRelacionLineaCreditoPN(double montoLineasCredito, double patrimonioNeto) {
        return (patrimonioNeto != 0) ? (montoLineasCredito / patrimonioNeto) * 100 : 0;
    }

    // Podés seguir agregando más funciones aquí si lo necesitás
}
