package com.netcracker.utility.service;

import com.amazonaws.services.s3.AmazonS3;
import com.netcracker.utility.config.AwsConfig;
import com.netcracker.utility.domain.FileInfo;
import com.netcracker.utility.domain.FileMapping;
import com.netcracker.utility.dto.FileWithLink;
import com.netcracker.utility.dto.UploadedLinks;
import com.netcracker.utility.repository.FileMappingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StorageServiceTest {

    StorageService storageService;

    FileMappingRepository fileMappingRepository;
    HashGenerator hashGenerator;
    AmazonS3 amazonS3;
    AwsConfig awsConfig;

    @BeforeEach
    void setUp() {
        fileMappingRepository = mock(FileMappingRepository.class);
        hashGenerator = mock(HashGenerator.class);
        amazonS3 = mock(AmazonS3.class, RETURNS_DEEP_STUBS);
        awsConfig = mock(AwsConfig.class);

        storageService = new AwsStorageService(
                awsConfig,
                amazonS3,
                hashGenerator,
                fileMappingRepository
        );

        when(awsConfig.getBucketName()).thenReturn("bucket");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void upload() {
    }

    @Test
    void getUploadedFileLinks_should_return_all_links() throws MalformedURLException {
        when(fileMappingRepository.findAll()).thenReturn(
                IntStream.range(1, 5).boxed()
                        .map(integer -> "file-name-" + integer)
                        .map(fileKey -> new FileMapping(new FileInfo(fileKey + ".pdf", fileKey)))
                        .collect(Collectors.toList())
        );


        for (int i = 1; i <= 4; i++) {
            when(amazonS3.getUrl(ArgumentMatchers.anyString(), eq("file-name-" + i)))
                    .thenReturn(new URL("https://amazon.com/s3/bucket/file-name-%d.pdf".formatted(i)));
        }


        final UploadedLinks uploadedFileLinks = storageService.getUploadedFileLinks();

        assertThat(uploadedFileLinks).isNotNull();

        var fileWithLinks = uploadedFileLinks.getFileWithLinks();

        assertThat(fileWithLinks).hasSize(4);
        assertThat(fileWithLinks).containsAll(
                IntStream.range(1, 5).boxed()
                        .map(integer -> "file-name-" + integer + ".pdf")
                        .map(name -> new FileWithLink(name, "https://amazon.com/s3/bucket/" + name))
                        .collect(Collectors.toList())
        );
    }


    @Test
    void getUploadedFileLinks_should_return_all_links_but_with_default_when_failure() throws MalformedURLException {
        when(fileMappingRepository.findAll()).thenReturn(
                IntStream.range(1, 5).boxed()
                        .map(integer -> "file-name-" + integer)
                        .map(fileKey -> new FileMapping(new FileInfo(fileKey + ".pdf", fileKey)))
                        .collect(Collectors.toList())
        );


        for (int i = 1; i <= 3; i++) {
            when(amazonS3.getUrl(ArgumentMatchers.anyString(), eq("file-name-" + i)))
                    .thenReturn(new URL("https://amazon.com/s3/bucket/file-name-%d.pdf".formatted(i)));
        }

        when(amazonS3.getUrl(ArgumentMatchers.anyString(), eq("file-name-4")))
                .thenThrow(new RuntimeException("Some Amazon Exception"));


        final UploadedLinks uploadedFileLinks = storageService.getUploadedFileLinks();

        assertThat(uploadedFileLinks).isNotNull();

        var fileWithLinks = uploadedFileLinks.getFileWithLinks();

        assertThat(fileWithLinks).hasSize(4);

        final List<FileWithLink> expectedResponse = IntStream.range(1, 4).boxed()
                                                            .map(integer -> "file-name-" + integer + ".pdf")
                                                            .map(name -> new FileWithLink(name, "https://amazon.com/s3/bucket/" + name))
                                                            .collect(Collectors.toList());
        expectedResponse.add(new FileWithLink("file-name-4.pdf", "Could Not Fetch File Link. Please try again."));
        
        assertThat(fileWithLinks).containsAll(expectedResponse);
    }
}