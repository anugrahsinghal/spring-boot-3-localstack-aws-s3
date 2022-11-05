package com.netcracker.utility.controller;

import com.netcracker.utility.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class FileUploadController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!MediaType.APPLICATION_PDF.toString().equals(file.getContentType())) {
            redirectAttributes.addFlashAttribute("msg",
                    "Only PDF files accepted!".formatted(file.getOriginalFilename()));
            return "redirect:/";
        }

        storageService.upload(file);

        redirectAttributes.addFlashAttribute("msg",
                "Successfully uploaded %s!".formatted(file.getOriginalFilename()));

        return "redirect:/";
    }
}
