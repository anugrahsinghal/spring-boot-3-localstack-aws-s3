package com.netcracker.utility.controller;

import com.netcracker.utility.dto.UploadedLinks;
import com.netcracker.utility.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FileListingController {

    private final StorageService storageService;

    @GetMapping("/")
    public String uploadedFiles(Model model) {
        final UploadedLinks uploadedFileLinks = storageService.getUploadedFileLinks();

        log.info("uploadedFileLinks = {}", uploadedFileLinks);

        model.addAttribute("uploadedLinks", uploadedFileLinks);

        return "index";
    }

}
