package br.edu.fateczl.tcc.bdd.steps;

import br.edu.fateczl.tcc.TccApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
