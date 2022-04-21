package ru.deyev.credit.dossier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.deyev.credit.dossier.configuration.EmailConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EmailConfigurationProperties.class)
public class DossierApplication {

    public static void main(String[] args) {
        SpringApplication.run(DossierApplication.class, args);
    }

}
