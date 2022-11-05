package com.netcracker.utility.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.netcracker.utility.config.AwsConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsStorageService implements StorageService {

    private final AwsConfig awsConfig;
    private final AmazonS3 amazonS3;
    private final HashGenerator hashGenerator;


    @PostConstruct
    void createBucket() {
        if (!amazonS3.doesBucketExistV2(awsConfig.getBucketName())) {
            amazonS3.createBucket(awsConfig.getBucketName());
        }
    }

    @Override
    @SneakyThrows
    public Object upload(MultipartFile file) {

        //noinspection UnnecessaryLocalVariable
        PutObjectResult putObjectResult = amazonS3.putObject(awsConfig.getBucketName(),
                "%s--%s".formatted(file.getName(), hashGenerator.getNonRepeatableHash().value()),
                file.getInputStream(),
                objectMetadata(file)
        );

        return putObjectResult;
    }

    private ObjectMetadata objectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        objectMetadata.getUserMetadata().put("extension", "pdf");

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
