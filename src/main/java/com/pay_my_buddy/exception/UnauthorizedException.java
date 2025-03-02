package com.pay_my_buddy.exception;

/**
 * Exception personnalisée pour gérer les accès non autorisés.
 * <p>
 * Cette exception est levée lorsqu'un utilisateur tente d'accéder à une ressource
 * ou d'effectuer une action sans les permissions requises.
 * </p>
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructeur de l'exception UnauthorizedException.
     *
     * @param message Message décrivant la raison de l'accès non autorisé.
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
