package com.qip.engine;

import com.qip.jpa.entities.Empresa;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class FinancialRulesEngine {
    public static void evaluar(EvaluacionFinanciera eval, Empresa empresa) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession kSession = kc.newKieSession("ksession-rules");

        kSession.setGlobal("empresa", empresa);
        kSession.insert(eval);
        kSession.fireAllRules();
        kSession.dispose();
    }
}
