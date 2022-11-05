package com.netcracker.utility.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.netcracker.utility.config.AwsConfig;
import com.netcracker.utility.domain.FileInfo;
import com.netcracker.utility.domain.FileMapping;
import com.netcracker.utility.dto.FileWithLink;
import com.netcracker.utility.dto.UploadedLinks;
import com.netcracker.utility.repository.FileMappingRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsStorageService implements StorageService {

    public static final boolean FAILED_STATUS = true;

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
                "%s--%s".formatted(file.getOriginalFilename(), hashGenerator.getNonRepeatableHash().value())
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
    public UploadedLinks getUploadedFileLinks() {
        final var allFileMappings = fileMappingRepository.findAll();

        final var fileUrlFutures =
                allFileMappings.stream()
                        .map(getFileUrlFuture())
                        .toArray(CompletableFuture[]::new);

        allOf(fileUrlFutures).join();

        final var futureByStatus = Arrays.stream(fileUrlFutures)
                                           .collect(Collectors.partitioningBy(CompletableFuture::isCompletedExceptionally));

        if (!futureByStatus.get(FAILED_STATUS).isEmpty()) {
            log.error("Could not fetch url for '{}' files", futureByStatus.get(FAILED_STATUS).size());
        }

        final var fileWithLinkList = Arrays.stream(fileUrlFutures)
                                             .map(CompletableFuture::join)
                                             .map(FileWithLink.class::cast)
                                             .collect(Collectors.toList());

        return new UploadedLinks(fileWithLinkList);
    }

    private Function<FileMapping, CompletableFuture<FileWithLink>> getFileUrlFuture() {
        return fileMapping ->
                       supplyAsync(() -> new FileWithLink(
                               fileMapping.getFileInfo().getOriginalName(),
                               amazonS3.getUrl(
                                       awsConfig.getBucketName(),
                                       fileMapping.getFileInfo().getStorageKey()
                               ).toExternalForm())
                       ).exceptionally(throwable -> {
                           // when we cannot get a file link we send back a default object
                           log.warn("Could Not Fetch File Link for key {}", fileMapping.getFileInfo().getStorageKey());
                           return new FileWithLink(
                                   fileMapping.getFileInfo().getOriginalName(),
                                   "Could Not Fetch File Link. Please try again."
                           );
                       });
    }
}
