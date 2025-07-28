package com.qip.engine;

public class ResultadoAnalisis {
    public int puntajeTotal;
    public double lineaOtorgada;
    public String comentario;
    public boolean requiereGarantia;

    public ResultadoAnalisis(int puntajeTotal, double lineaOtorgada, String comentario, boolean requiereGarantia) {
        this.puntajeTotal = puntajeTotal;
        this.lineaOtorgada = lineaOtorgada;
        this.comentario = comentario;
        this.requiereGarantia = requiereGarantia;
    }

    public static ResultadoAnalisis noOtorgarLinea(String motivo) {
        return new ResultadoAnalisis(0, 0.0, motivo, true);
    }
}
