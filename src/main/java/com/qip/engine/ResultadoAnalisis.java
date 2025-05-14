package com.qip.engine;

public class ResultadoAnalisis {
    public int puntajeTotal;
    public double lineaOtorgada;
    public String comentario;

    public ResultadoAnalisis(int puntajeTotal, double lineaOtorgada, String comentario) {
        this.puntajeTotal = puntajeTotal;
        this.lineaOtorgada = lineaOtorgada;
        this.comentario = comentario;
    }

    public static ResultadoAnalisis noOtorgarLinea(String motivo) {
        return new ResultadoAnalisis(0, 0.0, "No se otorga línea: " + motivo);
    }

    // Getters
}
