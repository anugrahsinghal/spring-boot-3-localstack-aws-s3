package com.netcracker.utility.service;

import com.netcracker.utility.dto.UploadedLinks;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    Object upload(MultipartFile file);

    Object download(Object someId);

    UploadedLinks getUploadedFileLinks();

}
