package com.netcracker.utility.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.netcracker.utility.config.AwsConfig;
import com.netcracker.utility.domain.FileInfo;
import com.netcracker.utility.domain.FileMapping;
import com.netcracker.utility.dto.FileWithLink;
import com.netcracker.utility.dto.UploadedLinks;
import com.netcracker.utility.repository.FileMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest
@Testcontainers
class StorageServiceIntegrationTest {

    FileMappingRepository fileMappingRepository;
    HashGenerator hashGenerator;

    @Autowired
    AmazonS3 amazonS3;

    @Autowired
    AwsConfig awsConfig;
    DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:1.2.0");
    @Autowired
    private StorageService storageService;
    // will be started before and stopped after each test method
    @Container
    private LocalStackContainer localstack = new LocalStackContainer(localstackImage)
                                                     .withServices(S3);

    // will be shared between test methods
    // @Container
    // private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer();

    @Bean
    AmazonS3 amazonS3() {
        final EndpointConfiguration endpointConfiguration = new EndpointConfiguration(
                localstack.getEndpointOverride(S3).toString(),
                localstack.getRegion()
        );
        return AmazonS3ClientBuilder
                       .standard()
                       .withEndpointConfiguration(endpointConfiguration)
                       .withCredentials(
                               new AWSStaticCredentialsProvider(
                                       new BasicAWSCredentials(localstack.getAccessKey(), localstack.getSecretKey())
                               )
                       )
                       .build();
    }


    @BeforeEach
    void setUp() {
        System.out.println("awsConfig.getBucketName() = " + awsConfig.getBucketName());
        if (amazonS3.doesBucketExistV2(awsConfig.getBucketName())) {
            amazonS3.deleteObjects(new DeleteObjectsRequest(awsConfig.getBucketName()));
            amazonS3.deleteBucket(awsConfig.getBucketName());
        }
        amazonS3.createBucket(awsConfig.getBucketName());
    }
    //     fileMappingRepository = mock(FileMappingRepository.class);
    //     hashGenerator = mock(HashGenerator.class);
    //     amazonS3 = mock(AmazonS3.class, RETURNS_DEEP_STUBS);
    //     awsConfig = mock(AwsConfig.class);
    //
    //     storageService = new AwsStorageService(
    //             awsConfig,
    //             amazonS3,
    //             hashGenerator,
    //             fileMappingRepository
    //     );
    //
    //     when(awsConfig.getBucketName()).thenReturn("bucket");
    // }

    // @AfterEach
    // void tearDown() {
    // }

    @Test
    void upload() {

        storageService.upload(new MockMultipartFile("some-file.pdf", "some-file.pdf", MediaType.APPLICATION_PDF_VALUE, new byte[0]));

        final UploadedLinks uploadedFileLinks = storageService.getUploadedFileLinks();

        assertThat(uploadedFileLinks).isNotNull();

        var fileWithLinks = uploadedFileLinks.getFileWithLinks();

        assertThat(fileWithLinks).hasSize(3);
    }

    @Disabled
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

    @Disabled
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