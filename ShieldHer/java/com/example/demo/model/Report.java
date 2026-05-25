package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "evidence_id")
    private Evidence evidence;
    
    private String reportType;
    private String status;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // Getters
    public Long getId() { return id; }
    public Evidence getEvidence() { return evidence; }
    public String getReportType() { return reportType; }
    public String getStatus() { return status; }
    public LocalDateTime getSentAt() { return sentAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEvidence(Evidence evidence) { this.evidence = evidence; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public void setStatus(String status) { this.status = status; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    @PrePersist
    public void prePersist() {
        sentAt = LocalDateTime.now();
        status = "PENDING";
    }
}