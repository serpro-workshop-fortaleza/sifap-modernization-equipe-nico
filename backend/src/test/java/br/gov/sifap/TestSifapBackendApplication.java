package br.gov.sifap;

import org.springframework.boot.SpringApplication;

/**
 * Ponto de entrada para rodar a aplicacao localmente com o PostgreSQL de teste.
 */
public class TestSifapBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(SifapBackendApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
