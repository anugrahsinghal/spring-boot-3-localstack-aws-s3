package com.netcracker.utility.controller;

import com.netcracker.utility.dto.UploadedLinks;
import com.netcracker.utility.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (!MediaType.APPLICATION_PDF.toString().equals(file.getContentType())) {
            return ResponseEntity.badRequest().body("Only PDF file types are allowed");
        }

        storageService.upload(file);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/links")
    public UploadedLinks uploadedFiles() {
        final UploadedLinks uploadedFileLinks = storageService.getUploadedFileLinks();

        log.info("uploadedFileLinks = {}", uploadedFileLinks);

        return uploadedFileLinks;
    }

}
