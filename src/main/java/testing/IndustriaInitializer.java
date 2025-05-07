package testing;


import com.qip.jpa.entities.Industria;
import com.qip.jpa.services.IndustriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class IndustriaInitializer implements CommandLineRunner {


    @Autowired
    private IndustriaService industriaService;

    @Override
    public void run(String... args) throws Exception {
        // Industries with category 1
        List<String> category1 = Arrays.asList(
                "Agroindustria",
                "Importadores",
                "Construccion",
                "Cemento/Acero",
                "Energia (Generacion/Distribucion)",
                "Proveedores Energia",
                "Logistica",
                "Servicios Publicos",
                "Petroleo y Gas"
        );

        // Industries with category 2
        List<String> category2 = Arrays.asList(
                "Laboratorios",
                "Supermercados",
                "Automotriz",
                "Alimenticias"
        );

        // Industries with category 3
        List<String> category3 = Arrays.asList(
                "Textiles",
                "Ensambladores TDF",
                "Comercios (Fravega, Megatone, etc)",
                "Entretenimiento-Cosnumo Masivo (Hoteles/Restaurantes)"
        );

        // Save industries for category 1
        saveIndustries(category1, "1");

        // Save industries for category 2
        saveIndustries(category2, "2");

        // Save industries for category 3
        saveIndustries(category3, "3");
    }

    private void saveIndustries(List<String> industries, String category) {
        for (String name : industries) {
            Industria industria = new Industria();
            industria.setNombre(name);
            industria.setCategoria(category);
            industriaService.saveIndustria(industria);
        }
    }
}