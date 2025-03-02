package com.pay_my_buddy.exception;

/**
 * Exception personnalisée pour gérer les ressources non trouvées.
 * <p>
 * Cette exception est levée lorsqu'une ressource demandée (comme un utilisateur ou une transaction)
 * n'est pas trouvée dans la base de données.
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructeur de l'exception ResourceNotFoundException.
     *
     * @param message Message décrivant la ressource introuvable.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
