package org.karnak;


import org.karnak.data.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.karnak.data")
@EnableJpaRepositories("org.karnak.data")
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
