package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "photo_url")
    private String photoUrl;  // ← @Column எடுத்துட்டேன்

    @Column(name = "found_url", columnDefinition = "TEXT")  // ← இங்க மாத்தினேன்
    private String foundUrl;

    private String platform;

    @Column(name = "status")
    private String status;

    @Column(name = "found_at")
    private LocalDateTime foundAt;

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getPhotoUrl() { return photoUrl; }
    public String getFoundUrl() { return foundUrl; }
    public String getPlatform() { return platform; }
    public String getStatus() { return status; }
    public LocalDateTime getFoundAt() { return foundAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setFoundUrl(String foundUrl) { this.foundUrl = foundUrl; }
    public void setPlatform(String platform) { this.platform = platform; }
    public void setStatus(String status) { this.status = status; }
    public void setFoundAt(LocalDateTime foundAt) { this.foundAt = foundAt; }

    @PrePersist
    public void prePersist() {
        foundAt = LocalDateTime.now();
        status = "FOUND";
    }
}