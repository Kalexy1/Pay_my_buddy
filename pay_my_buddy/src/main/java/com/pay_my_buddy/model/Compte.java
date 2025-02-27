package com.pay_my_buddy.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Entité représentant un compte utilisateur.
 * <p>
 * Chaque compte est associé à un utilisateur et possède un solde.
 * </p>
 */
@Entity
public class Compte {

    /**
     * Identifiant unique du compte.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    /**
     * Utilisateur associé au compte.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Solde du compte.
     */
    private BigDecimal solde;

    /**
     * Constructeur permettant d'initialiser un compte avec un utilisateur.
     *
     * @param user2 Utilisateur associé au compte.
     */
    public Compte(User user2) {
		// TODO Auto-generated constructor stub
	}

    /**
     * Constructeur par défaut de la classe Compte.
     */
	public Compte() {
		// TODO Auto-generated constructor stub
	}

    /**
     * Retourne l'identifiant du compte.
     *
     * @return Identifiant du compte.
     */
	public BigInteger getId() {
        return id;
    }

    /**
     * Définit l'identifiant du compte.
     *
     * @param id Nouvel identifiant du compte.
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Retourne l'utilisateur associé au compte.
     *
     * @return Utilisateur du compte.
     */
    public User getUser() {
        return user;
    }

    /**
     * Associe un utilisateur au compte.
     *
     * @param user Nouvel utilisateur du compte.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Retourne le solde du compte.
     *
     * @return Solde du compte.
     */
    public BigDecimal getSolde() {
        return solde;
    }

    /**
     * Met à jour le solde du compte.
     *
     * @param solde Nouveau solde du compte.
     */
    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }
}
