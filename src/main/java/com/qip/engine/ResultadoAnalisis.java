package com.qip.engine;

import java.util.List;

public class ResultadoAnalisis {
    public int puntajeTotal;
    public double lineaOtorgada;
    public String comentario;
    public boolean requiereGarantia;
    public List<String> pasos;

    public ResultadoAnalisis(int puntajeTotal, double lineaOtorgada, String comentario, boolean requiereGarantia, List<String> pasos) {
        this.puntajeTotal = puntajeTotal;
        this.lineaOtorgada = lineaOtorgada;
        this.comentario = comentario;
        this.requiereGarantia = requiereGarantia;
        this.pasos = pasos;
    }

    public static ResultadoAnalisis noOtorgarLinea(String motivo, List<String> pasos) {
        return new ResultadoAnalisis(0, 0.0, motivo, true, pasos);
    }
}
