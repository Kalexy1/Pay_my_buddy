package com.pay_my_buddy.security;

import com.pay_my_buddy.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implémentation personnalisée de {@link UserDetails} pour gérer l'authentification des utilisateurs.
 * <p>
 * Cette classe permet d'intégrer l'entité {@link User} au système de sécurité Spring Security.
 * </p>
 */
public class CustomUserDetails implements UserDetails {

    /**
     * Instance de l'utilisateur authentifié.
     */
    private final User user;

    /**
     * Constructeur de la classe {@code CustomUserDetails}.
     *
     * @param user Utilisateur dont les informations d'authentification sont utilisées.
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Retourne les autorités de l'utilisateur (non utilisées ici).
     *
     * @return Une liste vide car l'application ne gère pas de rôles pour le moment.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Retourne le mot de passe de l'utilisateur.
     *
     * @return Mot de passe de l'utilisateur.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Retourne le nom d'utilisateur (ici l'email de l'utilisateur).
     *
     * @return Adresse email de l'utilisateur.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indique si le compte de l'utilisateur est expiré.
     *
     * @return {@code true} car la gestion de l'expiration des comptes n'est pas implémentée.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indique si le compte de l'utilisateur est verrouillé.
     *
     * @return {@code true} car la gestion du verrouillage des comptes n'est pas implémentée.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indique si les informations d'identification de l'utilisateur sont expirées.
     *
     * @return {@code true} car la gestion de l'expiration des identifiants n'est pas implémentée.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indique si l'utilisateur est activé.
     *
     * @return {@code true} car la gestion de l'activation des comptes n'est pas implémentée.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
