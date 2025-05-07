package com.qip.engine;


public class EvaluacionFinanciera {

    private Integer industriaScore;
    private Integer scoreNosis;
    private Integer nfdEbitdaScore;
    private Integer coberturaInteresesScore;
    private Integer margenEbitdaScore;
    private Integer cantidadBancosScore;
    private Integer sitBcraScore;
    private Integer chequesRechazadosScore;

    private Integer puntajeTotal;
    private Integer vecesPromedioVentas;
    private String recomendacion;

    public Integer getIndustriaScore() {
        return industriaScore;
    }

    public void setIndustriaScore(Integer industriaScore) {
        this.industriaScore = industriaScore;
    }

    public Integer getScoreNosis() {
        return scoreNosis;
    }

    public void setScoreNosis(Integer scoreNosis) {
        this.scoreNosis = scoreNosis;
    }

    public Integer getNfdEbitdaScore() {
        return nfdEbitdaScore;
    }

    public void setNfdEbitdaScore(Integer nfdEbitdaScore) {
        this.nfdEbitdaScore = nfdEbitdaScore;
    }

    public Integer getCoberturaInteresesScore() {
        return coberturaInteresesScore;
    }

    public void setCoberturaInteresesScore(Integer coberturaInteresesScore) {
        this.coberturaInteresesScore = coberturaInteresesScore;
    }

    public Integer getMargenEbitdaScore() {
        return margenEbitdaScore;
    }

    public void setMargenEbitdaScore(Integer margenEbitdaScore) {
        this.margenEbitdaScore = margenEbitdaScore;
    }

    public Integer getCantidadBancosScore() {
        return cantidadBancosScore;
    }

    public void setCantidadBancosScore(Integer cantidadBancosScore) {
        this.cantidadBancosScore = cantidadBancosScore;
    }

    public Integer getSitBcraScore() {
        return sitBcraScore;
    }

    public void setSitBcraScore(Integer sitBcraScore) {
        this.sitBcraScore = sitBcraScore;
    }

    public Integer getChequesRechazadosScore() {
        return chequesRechazadosScore;
    }

    public void setChequesRechazadosScore(Integer chequesRechazadosScore) {
        this.chequesRechazadosScore = chequesRechazadosScore;
    }

    public Integer getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(Integer puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    public Integer getVecesPromedioVentas() {
        return vecesPromedioVentas;
    }

    public void setVecesPromedioVentas(Integer vecesPromedioVentas) {
        this.vecesPromedioVentas = vecesPromedioVentas;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }
}
