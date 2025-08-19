package com.littlehikerfriends.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hike_id", nullable = false)
    private Hike hike;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // 기본 생성자
    public Message() {
        this.createdAt = LocalDateTime.now();
    }
    
    // 편의 생성자
    public Message(Hike hike, User user, String content) {
        this.hike = hike;
        this.user = user;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
    
    // 위치 기반 메시지 생성자
    public Message(Hike hike, User user, String content, Location location) {
        this.hike = hike;
        this.user = user;
        this.content = content;
        this.location = location;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Hike getHike() { return hike; }
    public void setHike(Hike hike) { this.hike = hike; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
