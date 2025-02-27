package com.pay_my_buddy.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private BigDecimal solde;

    public Compte(User user2) {
		// TODO Auto-generated constructor stub
	}

	public Compte() {
		// TODO Auto-generated constructor stub
	}

	public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }
}
