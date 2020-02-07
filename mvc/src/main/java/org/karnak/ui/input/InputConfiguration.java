package org.karnak.ui.input;

import javax.annotation.PostConstruct;

import org.karnak.data.input.InputRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InputConfiguration {
    private static InputConfiguration instance;

    public static InputConfiguration getInstance() {
        return instance;
    }

    @Autowired
    private InputRepository inputRepository;

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }
    
    @Bean("InputNodes")
    public InputRepository getInputRepository() {
        return inputRepository;
    }
}
