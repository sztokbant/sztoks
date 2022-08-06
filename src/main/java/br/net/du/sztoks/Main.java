package br.net.du.sztoks;

import br.net.du.sztoks.persistence.CustomRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
public class Main {

    public static void main(final String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
