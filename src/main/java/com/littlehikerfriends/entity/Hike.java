package com.littlehikerfriends.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hikes")
public class Hike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "mountain_name", nullable = false)
    private String mountainName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HikeStatus status = HikeStatus.PLANNED;
    
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;
    
    @Column(name = "end_at")
    private LocalDateTime endAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // 연관관계 매핑
    @OneToMany(mappedBy = "hike", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HikeMember> hikeMembers;
    
    @OneToMany(mappedBy = "hike", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
    
    // 기본 생성자
    public Hike() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getMountainName() { return mountainName; }
    public void setMountainName(String mountainName) { this.mountainName = mountainName; }
    
    public HikeStatus getStatus() { return status; }
    public void setStatus(HikeStatus status) { this.status = status; }
    
    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    
    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<HikeMember> getHikeMembers() { return hikeMembers; }
    public void setHikeMembers(List<HikeMember> hikeMembers) { this.hikeMembers = hikeMembers; }
    
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}
