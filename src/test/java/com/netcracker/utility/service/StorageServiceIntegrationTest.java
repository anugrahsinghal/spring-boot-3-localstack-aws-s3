package com.netcracker.utility.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.netcracker.utility.config.AwsConfig;
import com.netcracker.utility.dto.FileWithLink;
import com.netcracker.utility.dto.UploadedLinks;
import com.netcracker.utility.repository.FileMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Slf4j
// @SpringBootTest(properties = "debug=true")
@SpringBootTest//({"spring.main.allow-bean-definition-overriding=true", "debug=true"})
//@Import(StorageServiceIntegrationTest.MockAmazonS3.class)
@Testcontainers(disabledWithoutDocker = true)
class StorageServiceIntegrationTest {

    public static final String TEST_BUCKET = "some-random-bucket";
    static DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:1.2.0");
    // will be started before and stopped after each test method
    @Container
    private static LocalStackContainer localstack = new LocalStackContainer(localstackImage).withServices(S3).withEnv("DEBUG", "1");
    @Autowired
    AmazonS3 amazonS3;
    @Autowired
    AwsConfig awsConfig;
    @Autowired
    FileMappingRepository repository;
    @Autowired
    private StorageService storageService;

    @DynamicPropertySource
    static void setLocalstackBucket(DynamicPropertyRegistry registry) {
        registry.add("aws.bucket-name", () -> TEST_BUCKET);
    }

    @BeforeEach
    void setUp() {
        // System.out.println();
        // System.out.println(localstack.getPortBindings());
        // System.out.println();
        // System.out.println("awsConfig.getBucketName() = " + awsConfig.getBucketName());
        boolean doesBucketExistV2 = amazonS3.doesBucketExistV2(awsConfig.getBucketName());
        // System.out.println(doesBucketExistV2);
        if (doesBucketExistV2) {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(awsConfig.getBucketName());
            ObjectListing listObjects = amazonS3.listObjects(listObjectsRequest);

            listObjects.getObjectSummaries().forEach(objSummary -> {
                // System.out.println(objSummary);
                amazonS3.deleteObject(new DeleteObjectRequest(objSummary.getBucketName(),
                        objSummary.getKey()));
            });

            amazonS3.deleteBucket(awsConfig.getBucketName());
            repository.deleteAll();


            // boolean doesBucketExistV2AfterDelete = amazonS3.doesBucketExistV2(awsConfig.getBucketName());
            // System.out.println("doesBucketExistV2AfterDelete = " + doesBucketExistV2AfterDelete);
        }
        Bucket createBucket = amazonS3.createBucket(awsConfig.getBucketName());
        // System.out.println("createBucket = " + createBucket);
    }

    @Test
    void upload() {

        storageService.upload(new MockMultipartFile("some-file.pdf", "some-file.pdf",
                MediaType.APPLICATION_PDF_VALUE, new byte[0]));

        final UploadedLinks uploadedFileLinks = storageService.getUploadedFileLinks();

        assertThat(uploadedFileLinks).isNotNull();

        var fileWithLinks = uploadedFileLinks.getFileWithLinks();

        assertThat(fileWithLinks).hasSize(1);
        assertThat(fileWithLinks.get(0)).extracting(FileWithLink::fileName).isEqualTo("some-file.pdf");
        assertThat(fileWithLinks.get(0)).extracting(FileWithLink::fileLink)
                .has(new Condition<>(s -> s.contains(TEST_BUCKET), "link should have bucket name"));
    }

    @TestConfiguration
    public static class MockAmazonS3 {
        @Primary
        @Bean
        AmazonS3 testAmazonS3() {
            final EndpointConfiguration endpointConfiguration = new EndpointConfiguration(
                    localstack.getEndpointOverride(S3).toString(),
                    localstack.getRegion());
            return AmazonS3ClientBuilder
                           .standard()
                           .withEndpointConfiguration(endpointConfiguration)
                           .withCredentials(
                                   new AWSStaticCredentialsProvider(
                                           new BasicAWSCredentials(
                                                   localstack.getAccessKey(),
                                                   localstack.getSecretKey())))
                           .build();
        }

    }
}