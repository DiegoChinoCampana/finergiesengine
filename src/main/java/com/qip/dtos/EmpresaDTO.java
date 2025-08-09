package com.qip.dtos;

import com.qip.jpa.entities.Empresa;
import com.qip.jpa.entities.DatosDeBalance;
import com.qip.jpa.entities.PostBalance;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String cuit;
    private Integer scoreNosis;
    private String industria;
    private List<String> ejerciciosBalance;
    private List<String> ejerciciosPostBalance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public Integer getScoreNosis() {
        return scoreNosis;
    }

    public void setScoreNosis(Integer scoreNosis) {
        this.scoreNosis = scoreNosis;
    }

    public String getIndustria() {
        return industria;
    }

    public void setIndustria(String industria) {
        this.industria = industria;
    }

    public List<String> getEjerciciosBalance() {
        return ejerciciosBalance;
    }

    public void setEjerciciosBalance(List<String> ejerciciosBalance) {
        this.ejerciciosBalance = ejerciciosBalance;
    }

    public List<String> getEjerciciosPostBalance() {
        return ejerciciosPostBalance;
    }

    public void setEjerciciosPostBalance(List<String> ejerciciosPostBalance) {
        this.ejerciciosPostBalance = ejerciciosPostBalance;
    }

    // Constructor
    public EmpresaDTO(Empresa empresa) {
        this.id = empresa.getId();
        this.nombre = empresa.getNombre();
        this.cuit = empresa.getCuit();
        this.scoreNosis = empresa.getScoreNosis();
        this.industria = empresa.getIndustria() != null ? empresa.getIndustria().getNombre() : null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


        this.ejerciciosBalance = empresa.getDatosDeBalances().stream()
                .map(b -> b.getEjercicio() != null ? b.getEjercicio().format(formatter) : null)
                .collect(Collectors.toList());

        this.ejerciciosPostBalance = empresa.getPostBalances().stream()
                .map(p -> p.getEjercicio() != null ? p.getEjercicio().format(formatter) : null)
                .collect(Collectors.toList());



    }

    public EmpresaDTO() {
    }

    // Getters y setters (o anotaciones Lombok si usás)
}

