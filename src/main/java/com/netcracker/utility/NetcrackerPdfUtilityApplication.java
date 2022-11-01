package com.netcracker.utility;

import com.netcracker.utility.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
public class NetcrackerPdfUtilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetcrackerPdfUtilityApplication.class, args);
    }

}

