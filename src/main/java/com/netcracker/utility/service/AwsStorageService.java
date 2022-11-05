package com.netcracker.utility.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.netcracker.utility.config.AwsConfig;
import com.netcracker.utility.domain.FileInfo;
import com.netcracker.utility.domain.FileMapping;
import com.netcracker.utility.repository.FileMappingRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsStorageService implements StorageService {

    private final AwsConfig awsConfig;
    private final AmazonS3 amazonS3;
    private final HashGenerator hashGenerator;
    private final FileMappingRepository fileMappingRepository;


    @PostConstruct
    void createBucket() {
        if (!amazonS3.doesBucketExistV2(awsConfig.getBucketName())) {
            amazonS3.createBucket(awsConfig.getBucketName());
        }
    }

    @Override
    @SneakyThrows
    public Object upload(MultipartFile file) {

        FileInfo fileInfo = new FileInfo(
                file.getOriginalFilename(),
                "%s--%s".formatted(file.getName(), hashGenerator.getNonRepeatableHash().value())
        );

        boolean doesObjectExist = amazonS3.doesObjectExist(awsConfig.getBucketName(), fileInfo.getStorageKey());
        if (doesObjectExist) {
            log.error("Could not save {}  with key {}", fileInfo.getOriginalName(), fileInfo.getStorageKey());
            throw new RuntimeException(
                    "Object cannot be saved as it already exists."
                    + "This condition should not be reached as hash should be a unique identifier."
                    + "Please try renaming your file for the time while we resolve this."
            );
        }
        PutObjectResult putObjectResult = amazonS3.putObject(awsConfig.getBucketName(),
                fileInfo.getStorageKey(),
                file.getInputStream(),
                objectMetadata(file)
        );

        try {
            fileMappingRepository.saveAndFlush(new FileMapping(fileInfo));
        } catch (Exception e) {
            throw new RuntimeException("Object Could not be saved", e);
        } finally {
            amazonS3.deleteObject(new DeleteObjectRequest(awsConfig.getBucketName(), fileInfo.getStorageKey()));
        }

        return putObjectResult;
    }

    private ObjectMetadata objectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        objectMetadata.getUserMetadata().put("extension", "pdf");
        objectMetadata.getUserMetadata().put("MimeType", MediaType.APPLICATION_PDF_VALUE.toLowerCase());

        return objectMetadata;
    }

    @Override
    public Object download(Object someId) {
        S3Object s3Object = amazonS3.getObject(awsConfig.getBucketName(), (String) someId);
        String filename = someId + "." + "pdf";
        Long contentLength = s3Object.getObjectMetadata().getContentLength();


        //        return DownloadedResource.builder().id(id).fileName(filename).contentLength(contentLength).inputStream(s3Object.getObjectContent())
        //                .build();
        return null;
    }
}
