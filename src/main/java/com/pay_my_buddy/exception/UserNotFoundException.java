package com.pay_my_buddy.exception;

/**
 * Exception personnalisée pour gérer les utilisateurs non trouvés.
 * <p>
 * Cette exception est levée lorsqu'un utilisateur recherché est introuvable dans la base de données.
 * </p>
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructeur de l'exception {@code UserNotFoundException}.
     *
     * @param message Message décrivant l'erreur.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
