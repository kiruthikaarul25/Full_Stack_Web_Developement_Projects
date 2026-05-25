package com.example.demo.Repository;

import com.example.demo.model.Report;
import com.example.demo.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByEvidence(Evidence evidence);
    List<Report> findByStatus(String status);
    List<Report> findByReportType(String reportType);
}