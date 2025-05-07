package com.qip.jpa.controller;


import com.qip.jpa.entities.Industria;
import com.qip.jpa.services.IndustriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/industrias")
public class IndustriaController {

    @Autowired
    private IndustriaService industriaService;

    @PostMapping
    public Industria createIndustria(@RequestBody Industria industria) {
        return industriaService.saveIndustria(industria);
    }

    @PostMapping("/batch")
    public List<Industria> createIndustrias(@RequestBody List<Industria> industrias) {
        return industriaService.saveAllIndustrias(industrias);
    }

    @GetMapping("/{id}")
    public Industria getIndustriaById(@PathVariable Long id) {
        return industriaService.getIndustriaById(id)
                .orElseThrow(() -> new IllegalArgumentException("Industria with ID " + id + " not found"));
    }

    @GetMapping
    public List<Industria> getAllIndustrias() {
        return industriaService.getAllIndustrias();
    }

    @PutMapping("/{id}")
    public Industria updateIndustria(@PathVariable Long id, @RequestBody Industria industriaDetails) {
        Industria industria = industriaService.getIndustriaById(id)
                .orElseThrow(() -> new IllegalArgumentException("Industria with ID " + id + " not found"));

        industria.setNombre(industriaDetails.getNombre());
        return industriaService.saveIndustria(industria);
    }

    @DeleteMapping("/{id}")
    public String deleteIndustria(@PathVariable Long id) {
        industriaService.deleteIndustria(id);
        return "Industria with ID " + id + " deleted successfully";
    }
}