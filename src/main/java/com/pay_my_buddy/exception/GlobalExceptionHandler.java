package com.pay_my_buddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour l'application.
 * <p>
 * Cette classe intercepte et gère les exceptions spécifiques afin de fournir
 * des réponses HTTP adaptées aux erreurs rencontrées.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions pour les ressources non trouvées.
     * <p>
     * Retourne une réponse HTTP 404 lorsque l'entité demandée est introuvable.
     * </p>
     *
     * @param ex L'exception {@link ResourceNotFoundException}.
     * @return Une réponse HTTP 404 avec un message d'erreur.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Gère les exceptions de validation des arguments de requête.
     * <p>
     * Retourne une réponse HTTP 400 avec une liste des erreurs de validation des champs.
     * </p>
     *
     * @param ex L'exception {@link MethodArgumentNotValidException}.
     * @return Une réponse HTTP 400 contenant les erreurs de validation des champs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Gère les exceptions générales de l'application.
     * <p>
     * Retourne une réponse HTTP 500 en cas d'erreur interne du serveur.
     * </p>
     *
     * @param ex      L'exception {@link Exception} capturée.
     * @param request L'objet {@link WebRequest} contenant les détails de la requête.
     * @return Une réponse HTTP 500 avec un message d'erreur.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne du serveur : " + ex.getMessage());
    }

    /**
     * Gère les exceptions de type {@link IllegalArgumentException}.
     * <p>
     * Retourne une réponse HTTP 400 lorsque les arguments fournis sont invalides.
     * </p>
     *
     * @param ex L'exception {@link IllegalArgumentException}.
     * @return Une réponse HTTP 400 avec un message d'erreur.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
