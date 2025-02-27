package com.pay_my_buddy;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application Pay My Buddy.
 * <p>
 * Cette classe initialise l'application Spring Boot et charge les variables d'environnement
 * depuis un fichier `.env` pour configurer la base de données.
 * </p>
 */
@SpringBootApplication
public class PayMyBuddyApplication {

    /**
     * Point d'entrée principal de l'application Pay My Buddy.
     * <p>
     * Charge les variables d'environnement depuis le fichier `DB.env` et
     * définit les propriétés système nécessaires à la connexion à la base de données,
     * avant de démarrer l'application Spring Boot.
     * </p>
     *
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                              .filename("DB.env") 
                              .load();

        System.setProperty("DB_URL", dotenv.get("spring.datasource.url"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(PayMyBuddyApplication.class, args);
    }
}
