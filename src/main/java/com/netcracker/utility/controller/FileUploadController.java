package com.netcracker.utility.controller;

import com.netcracker.utility.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (!MediaType.APPLICATION_PDF.toString().equals(file.getContentType())) {
            return ResponseEntity.badRequest().body("Only PDF file types are allwed");
        }

        Object upload = storageService.upload(file);

        return ResponseEntity.accepted().body(upload);
    }

}
