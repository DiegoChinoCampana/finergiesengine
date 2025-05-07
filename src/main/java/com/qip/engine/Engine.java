package com.qip.engine;

import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.Empresa;

public class Engine {
    public static void main(String[] args) {
        Empresa empresa = new Empresa();
        empresa.setNombre("Demo S.A.");

        DatosDeBalance balance2024 = new DatosDeBalance();
        balance2024.setVentasNetas(25020612.0);
        balance2024.setResultadoBruto(5657037.0);
        balance2024.setResultadoOperativo(3799214.0);
        balance2024.setAmortizacionesYDepreciaciones(643218.0);

        double margenBruto = (balance2024.getResultadoBruto() / balance2024.getVentasNetas()) * 100;
        double ebitda = balance2024.getResultadoOperativo() + balance2024.getAmortizacionesYDepreciaciones();
        double margenEbitda = (ebitda / balance2024.getVentasNetas()) * 100;

        System.out.println("Margen Bruto: " + String.format("%.2f", margenBruto) + "%");
        System.out.println("EBITDA: " + String.format("%,.0f", ebitda));
        System.out.println("Margen EBITDA: " + String.format("%.2f", margenEbitda) + "%");
    }
}
