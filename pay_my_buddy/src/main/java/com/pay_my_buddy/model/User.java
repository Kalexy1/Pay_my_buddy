package com.pay_my_buddy.model;

import jakarta.persistence.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un utilisateur de l'application.
 * <p>
 * Chaque utilisateur possède un identifiant unique, un email, un nom d'utilisateur,
 * un mot de passe et une liste d'amis.
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Identifiant unique de l'utilisateur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    /**
     * Adresse email unique de l'utilisateur.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Nom d'utilisateur.
     */
    @Column(nullable = false)
    private String username;

    /**
     * Mot de passe de l'utilisateur.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Liste des amis de l'utilisateur.
     * <p>
     * Relation ManyToMany représentant les connexions entre utilisateurs.
     * </p>
     */
    @ManyToMany
    @JoinTable(
        name = "connections",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();

    /**
     * Constructeur par défaut de la classe User.
     */
    public User() {
    }

    /**
     * Constructeur permettant d'initialiser un utilisateur avec son email, son nom et son mot de passe.
     *
     * @param email    Adresse email de l'utilisateur.
     * @param username Nom d'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     */
    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    /**
     * Retourne l'identifiant de l'utilisateur.
     *
     * @return Identifiant de l'utilisateur.
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Définit l'identifiant de l'utilisateur.
     *
     * @param id Nouvel identifiant de l'utilisateur.
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Retourne l'adresse email de l'utilisateur.
     *
     * @return Adresse email de l'utilisateur.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Met à jour l'adresse email de l'utilisateur.
     *
     * @param email Nouvelle adresse email de l'utilisateur.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retourne le nom d'utilisateur.
     *
     * @return Nom d'utilisateur.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Met à jour le nom d'utilisateur.
     *
     * @param username Nouveau nom d'utilisateur.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retourne le mot de passe de l'utilisateur.
     *
     * @return Mot de passe de l'utilisateur.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Met à jour le mot de passe de l'utilisateur.
     *
     * @param password Nouveau mot de passe de l'utilisateur.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retourne la liste des amis de l'utilisateur.
     *
     * @return Ensemble des amis de l'utilisateur.
     */
    public Set<User> getFriends() {
        return friends;
    }

    /**
     * Met à jour la liste des amis de l'utilisateur.
     *
     * @param friends Nouvelle liste d'amis de l'utilisateur.
     */
    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    /**
     * Ajoute un ami à la liste des amis de l'utilisateur.
     *
     * @param friend Ami à ajouter.
     */
    public void addFriend(User friend) {
        this.friends.add(friend);
    }

    /**
     * Supprime un ami de la liste des amis de l'utilisateur.
     *
     * @param friend Ami à supprimer.
     */
    public void removeFriend(User friend) {
        this.friends.remove(friend);
    }
}
