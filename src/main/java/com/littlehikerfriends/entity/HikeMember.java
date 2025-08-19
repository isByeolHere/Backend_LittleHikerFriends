package com.littlehikerfriends.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hike_members", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"hike_id", "user_id"}))
public class HikeMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hike_id", nullable = false)
    private Hike hike;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.MEMBER;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    // 기본 생성자
    public HikeMember() {
        this.joinedAt = LocalDateTime.now();
    }
    
    // 편의 생성자
    public HikeMember(Hike hike, User user, MemberRole role) {
        this.hike = hike;
        this.user = user;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Hike getHike() { return hike; }
    public void setHike(Hike hike) { this.hike = hike; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public MemberRole getRole() { return role; }
    public void setRole(MemberRole role) { this.role = role; }
    
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
