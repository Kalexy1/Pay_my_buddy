package com.pay_my_buddy.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigInteger;

/**
 * Entité représentant une connexion entre deux utilisateurs.
 * <p>
 * Cette classe est utilisée pour gérer les relations d'amitié entre les utilisateurs
 * de l'application.
 * </p>
 */
@Entity
@Table(name = "connections")
@Data
public class Connection {

    /**
     * Identifiant unique de la connexion.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    /**
     * Utilisateur initiant la connexion.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    /**
     * Ami ajouté par l'utilisateur.
     */
    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "id", nullable = false)
    private User friend;

    /**
     * Retourne l'identifiant de la connexion.
     *
     * @return Identifiant de la connexion.
     */
    public BigInteger getId() { return id; }

    /**
     * Définit l'identifiant de la connexion.
     *
     * @param id Nouvel identifiant de la connexion.
     */
    public void setId(BigInteger id) { this.id = id; }

    /**
     * Retourne l'utilisateur initiant la connexion.
     *
     * @return Utilisateur initiant la connexion.
     */
    public User getUser() { return user; }

    /**
     * Associe un utilisateur à la connexion.
     *
     * @param user Utilisateur initiant la connexion.
     */
    public void setUser(User user) { this.user = user; }

    /**
     * Retourne l'ami connecté à l'utilisateur.
     *
     * @return Ami de l'utilisateur.
     */
    public User getFriend() { return friend; }

    /**
     * Associe un ami à l'utilisateur.
     *
     * @param friend Ami de l'utilisateur.
     */
    public void setFriend(User friend) { this.friend = friend; }
}
