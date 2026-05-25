package com.example.demo.controller;

import com.example.demo.model.Evidence;
import com.example.demo.model.Report;
import com.example.demo.Repository.EvidenceRepository;
import com.example.demo.Repository.ReportRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.EvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/create/{evidenceId}")
    public ResponseEntity<?> createReport(
            @PathVariable("evidenceId") Long evidenceId,
            @RequestParam("reportType") String reportType,
            @RequestParam("ownerEmail") String ownerEmail) {
        try {
            Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

            emailService.sendRemovalRequest(
                ownerEmail,
                evidence.getPhotoUrl(),
                evidence.getFoundUrl()
            );

            Report report = new Report();
            report.setEvidence(evidence);
            report.setReportType(reportType);
            report.setStatus("SENT");
            reportRepository.save(report);

            return ResponseEntity.ok("Report sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportRepository.findAll());
    }
}