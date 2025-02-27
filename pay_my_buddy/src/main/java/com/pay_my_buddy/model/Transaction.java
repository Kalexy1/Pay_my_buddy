package com.pay_my_buddy.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Entité représentant une transaction entre deux utilisateurs.
 * <p>
 * Une transaction inclut un expéditeur, un destinataire, un montant,
 * une description et une date de création.
 * </p>
 */
@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    /**
     * Identifiant unique de la transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    /**
     * Utilisateur envoyant l'argent.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Utilisateur recevant l'argent.
     */
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * Montant de la transaction.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /**
     * Description de la transaction.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Date et heure de création de la transaction.
     * <p>
     * Cette valeur est automatiquement définie lors de l'insertion en base de données.
     * </p>
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Méthode appelée avant l'insertion en base de données pour définir la date de création.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Retourne l'identifiant de la transaction.
     *
     * @return Identifiant de la transaction.
     */
    public BigInteger getId() { return id; }

    /**
     * Définit l'identifiant de la transaction.
     *
     * @param id Nouvel identifiant de la transaction.
     */
    public void setId(BigInteger id) { this.id = id; }

    /**
     * Retourne l'utilisateur envoyant l'argent.
     *
     * @return Expéditeur de la transaction.
     */
    public User getSender() { return sender; }

    /**
     * Définit l'expéditeur de la transaction.
     *
     * @param sender Nouvel expéditeur de la transaction.
     */
    public void setSender(User sender) { this.sender = sender; }

    /**
     * Retourne l'utilisateur recevant l'argent.
     *
     * @return Destinataire de la transaction.
     */
    public User getReceiver() { return receiver; }

    /**
     * Définit le destinataire de la transaction.
     *
     * @param receiver Nouvel utilisateur recevant l'argent.
     */
    public void setReceiver(User receiver) { this.receiver = receiver; }

    /**
     * Retourne le montant de la transaction.
     *
     * @return Montant de la transaction.
     */
    public BigDecimal getAmount() { return amount; }

    /**
     * Définit le montant de la transaction.
     *
     * @param amount Nouveau montant de la transaction.
     */
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    /**
     * Retourne la description de la transaction.
     *
     * @return Description de la transaction.
     */
    public String getDescription() { return description; }

    /**
     * Définit la description de la transaction.
     *
     * @param description Nouvelle description de la transaction.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Retourne la date et l'heure de création de la transaction.
     *
     * @return Date et heure de création.
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Définit la date et l'heure de création de la transaction.
     *
     * @param createdAt Nouvelle date de création.
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
