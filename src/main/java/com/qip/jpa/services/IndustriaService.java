package com.qip.jpa.services;

import com.qip.jpa.entities.Industria;
import com.qip.jpa.repositories.IndustriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IndustriaService {

    @Autowired
    private IndustriaRepository industriaRepository;

    public Optional<Industria> getIndustriaById(Long id) {
        return industriaRepository.findById(id);
    }

    public List<Industria> saveAllIndustrias(List<Industria> industrias) {
        return industriaRepository.saveAll(industrias);
    }

    public Industria saveIndustria(Industria industria) {
        return industriaRepository.save(industria);
    }

    public void deleteIndustria(Long id) {
        industriaRepository.deleteById(id);
    }

    public Industria getIndustriaByNombre(String nombre) {
        return industriaRepository.findByNombre(nombre);
    }

    public List<Industria> getAllIndustrias() {
        return industriaRepository.findAll();
    }
}