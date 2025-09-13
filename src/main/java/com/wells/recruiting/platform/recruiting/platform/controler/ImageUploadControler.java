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
            "image/gif"
    );


    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadFile(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "No file selected"));
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid file type"));
        }

        String uploadDir = "C:\\Users\\Wells\\Documents\\uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        File dest = new File(dir, fileName);

        try {
            file.transferTo(dest);
            String imageUrl = "file:///C:/Users/Wells/Documents/uploads/" + fileName;
            return ResponseEntity.ok(java.util.Map.of("imageUrl", imageUrl));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "File upload failed"));
        }
    }

}
