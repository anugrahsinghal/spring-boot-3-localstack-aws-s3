package com.netcracker.utility.controller;

import com.netcracker.utility.dto.UploadedLinks;
import com.netcracker.utility.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileListingController {

    private final StorageService storageService;

    @PostMapping("/list")
    public ResponseEntity<UploadedLinks> uploadFile() {
        return ResponseEntity
                       .ok()
                       .body(storageService.getUploadedFileLinks());
    }

}
