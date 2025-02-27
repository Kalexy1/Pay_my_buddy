package com.pay_my_buddy.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigInteger;

@Entity
@Table(name = "connections")
@Data
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "id", nullable = false)
    private User friend;

    // Getters and Setters
    public BigInteger getId() { return id; }
    public void setId(BigInteger id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getFriend() { return friend; }
    public void setFriend(User friend) { this.friend = friend; }
}
