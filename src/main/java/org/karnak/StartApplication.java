package org.karnak;


import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.karnak.backend.config.AppConfig;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EntityScan("org.karnak.backend.data.entity")
@EnableJpaRepositories("org.karnak.backend.data.repo")
@EnableVaadin(value = "org.karnak")
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class, basePackages = "org.karnak")
public class StartApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(StartApplication.class);

    @Autowired
    private AppConfig myConfig;
    
    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("StartApplication...");
        log.info("using environment: " + myConfig.getEnvironment());
        log.info("name: " + myConfig.getName());
    }
}
