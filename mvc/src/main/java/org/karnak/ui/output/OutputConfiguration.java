package org.karnak.ui.output;

import javax.annotation.PostConstruct;

import org.karnak.data.output.OutputRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutputConfiguration {
    private static OutputConfiguration instance;

    public static OutputConfiguration getInstance() {
        return instance;
    }

    @Autowired
    private OutputRepository outputRepository;

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }

    @Bean("OutputNodes")
    public OutputRepository getOutputRepository() {
        return outputRepository;
    }
}
