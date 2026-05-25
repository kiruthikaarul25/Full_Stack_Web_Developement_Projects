package com.example.demo.controller;

import com.example.demo.model.Evidence;
import com.example.demo.model.User;
import com.example.demo.service.AIDetectionService;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.EmailService;
import com.example.demo.service.EvidenceService;
import com.example.demo.service.ImageSearchService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/photo")
@CrossOrigin(origins = "*")
public class PhotoController {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private EvidenceService evidenceService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageSearchService imageSearchService;
    @Autowired
    private AIDetectionService aiDetectionService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/upload/{userId}")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = cloudinaryService.uploadPhoto(file);
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            String searchResult = imageSearchService.searchImage(photoUrl);
            String aiResult = aiDetectionService.detectAI(photoUrl);

            String platform;
            String status;
            String foundUrl = "";

            boolean isAI = false;
            if (aiResult.contains("\"ai_generated\"")) {
                try {
                    int idx = aiResult.indexOf("\"ai_generated\":");
                    String sub = aiResult.substring(idx + 15, idx + 20).trim();
                    double aiScore = Double.parseDouble(sub.replaceAll("[^0-9.]", ""));
                    if (aiScore > 0.5) {
                        isAI = true;
                    }
                } catch (Exception e) {
                    isAI = false;
                }
            }

            boolean hasFace = aiResult.contains("\"faces\": [") && 
                !aiResult.contains("\"faces\": []") &&
                !aiResult.contains("\"faces\":[]");

            if (isAI) {
                platform = "🤖 AI Generated Image Detected!";
                status = "AI_GENERATED";
            } else if (searchResult.contains("\"visual_matches\"") && hasFace) {
                platform = "🚨 Exact Photo Found Online! Report Immediately!";
                status = "FOUND";
            } else if (searchResult.contains("\"image_results\"") && hasFace) {
                platform = "⚠️ Similar Images Found - Face Detected!";
                status = "SIMILAR";
            } else if (searchResult.contains("\"image_results\"") && !hasFace) {
                platform = "✅ Safe - No Face Match Found";
                status = "SAFE";
            } else {
                platform = "✅ Safe - Not Found Online";
                status = "SAFE";
            }

            // Cloudinary delete
            try {
                String publicId = "shieldher/" + photoUrl.substring(
                    photoUrl.lastIndexOf("/") + 1,
                    photoUrl.lastIndexOf(".")
                );
                cloudinaryService.deletePhoto(publicId);
            } catch (Exception e) {
                System.out.println("Delete error: " + e.getMessage());
            }

            Evidence evidence = new Evidence();
            evidence.setUser(user);
            evidence.setPhotoUrl(photoUrl);
            evidence.setStatus(status);
            evidence.setPlatform(platform);
            evidence.setFoundUrl(foundUrl);
            evidenceService.save(evidence);

        
           return ResponseEntity.ok(photoUrl);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/evidence/{userId}")
    public ResponseEntity<?> getEvidence(
            @PathVariable("userId") Long userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            List<Evidence> evidenceList = evidenceService.findByUser(user);
            return ResponseEntity.ok(evidenceList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/evidence/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {
        try {
            Evidence updated = evidenceService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}