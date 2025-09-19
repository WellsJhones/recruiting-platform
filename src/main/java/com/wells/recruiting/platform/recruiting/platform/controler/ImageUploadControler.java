package com.wells.recruiting.platform.recruiting.platform.controler;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class ImageUploadControler {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/gif");

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadFile(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "No file selected"));
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid file type"));
        }

        String uploadDir = "/var/www/html/uploads";
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to create upload directory"));
        }

        // Sanitize the original filename
        String originalName = file.getOriginalFilename();
        String sanitized = originalName == null ? "file"
                : originalName
                        .toLowerCase()
                        .replaceAll("\\s+", "_")
                        .replaceAll("[^a-z0-9._-]", "");
        String fileName = System.currentTimeMillis() + "-" + sanitized;

        File dest = new File(dir, fileName);

        try {
            file.transferTo(dest);
            String imageUrl = "https://wellsjhones.com.br/uploads/" + fileName;
            return ResponseEntity.ok(java.util.Map.of("imageUrl", imageUrl));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "File upload failed"));
        }
    }

}
