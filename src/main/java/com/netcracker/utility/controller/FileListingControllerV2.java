package com.netcracker.utility.controller;

import com.netcracker.utility.dto.UploadedLinks;
import com.netcracker.utility.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileListingControllerV2 {

    private final StorageService storageService;

    @GetMapping("/links")
    public UploadedLinks uploadedFiles() {
        final UploadedLinks uploadedFileLinks = storageService.getUploadedFileLinks();

        log.info("uploadedFileLinks = {}", uploadedFileLinks);

        return uploadedFileLinks;
    }

}
