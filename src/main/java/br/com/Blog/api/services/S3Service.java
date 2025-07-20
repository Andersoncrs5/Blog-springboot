package br.com.Blog.api.services;

import br.com.Blog.api.entities.Media;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Async
    public void enableBucketVersioning(String bucketName) {
        if (bucketName.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required"); }

        PutBucketVersioningRequest request = PutBucketVersioningRequest.builder()
                .bucket(bucketName)
                .versioningConfiguration(VersioningConfiguration.builder()
                        .status(BucketVersioningStatus.ENABLED)
                        .build())
                .build();

        s3Client.putBucketVersioning(request);
    }

    @Async
    public void suspendedBucketVersioning(String bucketName) {
        if (bucketName.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required"); }

        PutBucketVersioningRequest request = PutBucketVersioningRequest.builder()
                .bucket(bucketName)
                .versioningConfiguration(VersioningConfiguration.builder()
                        .status(BucketVersioningStatus.SUSPENDED)
                        .build())
                .build();

        s3Client.putBucketVersioning(request);
    }

    @Async
    public void createBucket(String bucketName) {
        if (bucketName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do bucket é obrigatório.");
        }

        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um bucket com o nome: " + bucketName);
        } catch (NoSuchBucketException _) {

        } catch (S3Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao verificar o bucket: " + e.getMessage(), e);
        }

        CreateBucketRequest request = CreateBucketRequest.builder().bucket(bucketName).build();

        try {
            s3Client.createBucket(request);
        } catch (S3Exception e) {
            if (e.statusCode() == HttpStatus.CONFLICT.value()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "O bucket '" + bucketName + "' já existe ou outro usuário é o proprietário.", e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar bucket S3: " + e.getMessage(), e);
        }
    }

    @Async
    public void deleteBucket(String bucketName) {
        DeleteBucketRequest request = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3Client.deleteBucket(request);
    }

    @Async
    public void bucketExists(String bucketName) {
        try {
            HeadBucketRequest head = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.headBucket(head);
        } catch (NoSuchBucketException e) {
            System.out.println("Bucket '" + bucketName + "' NÃO existe.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (S3Exception e) {
            System.err.println("Erro ao verificar a existência do bucket '" + bucketName + "': " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<String> listAllBuckets() {
        ListBucketsRequest request = ListBucketsRequest.builder().build();

        ListBucketsResponse response = s3Client.listBuckets(request);

        return response.buckets().stream().map(Bucket::name).collect(Collectors.toList());
    }

    @Async
    public void putObject(String bucketName, String key, MultipartFile file, User user, Post post)  {
        if (bucketName.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required");}
        if (key.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key is required");}
        if (file.isEmpty()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");}
        if (user.getId() <= 0) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is required");}
        if (post.getId() <= 0) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post is required");}

        Map<String, String> matadatas = new HashMap<>();
        matadatas.put("userId", String.valueOf(user.getId()));
        matadatas.put("postId", String.valueOf(post.getId()));

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .metadata(matadatas)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error the send file. try again later");
        }
    }

    @Async
    public void deleteObject(String bucketName, String key) {
        if (bucketName.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required");}
        if (key.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key is required");}

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    @Async
    public void deleteMultiObject(String bucketName, List<String> keys) {
        if (bucketName.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required");}
        if (keys.isEmpty()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "List of keys cannot be empty");}

        List<ObjectIdentifier> objectsToDelete = keys.stream().map(key -> ObjectIdentifier.builder().key(key).build()).collect(Collectors.toList());

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(d -> d.objects(objectsToDelete))
                .build();

        s3Client.deleteObjects(request);
    }

    @Async
    public void deleteObjectVersion(String bucketName, String key, String versionId) {
        if (bucketName.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required");}
        if (key.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key is required");}
        if (versionId.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Version id is required");}

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .versionId(versionId)
                .build();

        s3Client.deleteObject(request);
    }

    public URL generateLinkToDownloadFile(String bucketName, String key, long expirationDays) {
        if (bucketName.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required");}
        if (key.isBlank()) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key is required");}
        if (expirationDays <= 0 ) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "expiration Days id is required");}

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofDays(expirationDays))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url();
    }

    public ResponseEntity<byte[]> downloadSpecificObjectVersion(String bucketName, String key, String versionId) {
        if (bucketName.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required"); }
        if (key.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key is required"); }
        if (versionId.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Version ID is required"); }

        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        byte[] fileContent;

        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .versionId(versionId)
                    .build();
            HeadObjectResponse headResponse = s3Client.headObject(headRequest);
            if (headResponse.contentType() != null) {contentType = headResponse.contentType();}
        } catch (NoSuchKeyException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Versão '" + versionId + "' do arquivo '" + key + "' não encontrada.", e);
        }

        try (ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .versionId(versionId)
                .build())) {

            fileContent = s3ObjectStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", key + "_version_" + versionId.substring(0, Math.min(versionId.length(), 8))); // Limita o ID para legibilidade
            headers.setContentLength(fileContent.length);

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

        } catch (NoSuchKeyException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Versão '" + versionId + "' do arquivo '" + key + "' não encontrada.", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro de I/O ao baixar o arquivo: " + e.getMessage(), e);
        } catch (S3Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao baixar versão do arquivo S3: " + e.getMessage(), e);
        }
    }

    @Async
    public void applyLifeCyclePolicyMoveToStandardIAAndExpireCurrentVersions(String bucketName, String prefix, int days, int expDays) {
        if (bucketName.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required"); }
        if (prefix.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prefix is required"); }
        if (days <= 0) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Days is required"); }
        if (expDays <= 0) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiration Days is required"); }

        LifecycleRule rule = LifecycleRule.builder()
                .id("MoveToStandardIAAndExpireCurrentVersions")
                .filter(f -> f.prefix(prefix))
                .status("Enabled")
                .transitions(Transition.builder()
                        .days(days)
                        .storageClass(TransitionStorageClass.STANDARD_IA)
                        .build())
                .expiration(LifecycleExpiration.builder().days(expDays).build())
                .build();

        BucketLifecycleConfiguration configuration = BucketLifecycleConfiguration.builder().rules(rule).build();

        PutBucketLifecycleConfigurationRequest putLifecycleConfigurationRequest = PutBucketLifecycleConfigurationRequest.builder()
                .bucket(bucketName)
                .lifecycleConfiguration(configuration)
                .build();

        s3Client.putBucketLifecycleConfiguration(putLifecycleConfigurationRequest);
    }

    @Async
    public void applyLifeCyclePolicyExpireNonCurrentVersions(String bucketName, String prefix, int noncurrentDays) {
        if (bucketName.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required"); }
        if (prefix.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prefix is required"); }
        if (noncurrentDays <= 0) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Days is required"); }

        LifecycleRule rule = LifecycleRule.builder()
                .id("ExpireNonCurrentVersions")
                .filter(f -> f.prefix(prefix))
                .status("Enabled")
                .noncurrentVersionTransitions(NoncurrentVersionTransition.builder()
                        .noncurrentDays(noncurrentDays)
                        .storageClass(TransitionStorageClass.GLACIER)
                        .build())
                .noncurrentVersionExpiration(NoncurrentVersionExpiration.builder()
                        .noncurrentDays(365)
                        .build())
                .build();

        BucketLifecycleConfiguration configuration = BucketLifecycleConfiguration.builder().rules(rule).build();

        PutBucketLifecycleConfigurationRequest putLifecycleConfigurationRequest = PutBucketLifecycleConfigurationRequest.builder()
                .bucket(bucketName)
                .lifecycleConfiguration(configuration)
                .build();

        s3Client.putBucketLifecycleConfiguration(putLifecycleConfigurationRequest);
    }

    @Async
    public void applyLifeCyclePolicyAbortIncompleteMultipartUploads(String bucketName, String prefix, int days) {
        if (bucketName.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bucket name is required"); }
        if (prefix.isBlank()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prefix is required"); }
        if (days <= 0) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Days is required"); }

        LifecycleRule rule = LifecycleRule.builder()
                .id("AbortIncompleteMultipartUploads")
                .filter(f -> f.prefix(prefix))
                .status("Enabled")
                .abortIncompleteMultipartUpload(AbortIncompleteMultipartUpload.builder()
                        .daysAfterInitiation(days)
                        .build())
                .build();

        BucketLifecycleConfiguration configuration = BucketLifecycleConfiguration.builder().rules(rule).build();

        PutBucketLifecycleConfigurationRequest putLifecycleConfigurationRequest = PutBucketLifecycleConfigurationRequest.builder()
                .bucket(bucketName)
                .lifecycleConfiguration(configuration)
                .build();

        s3Client.putBucketLifecycleConfiguration(putLifecycleConfigurationRequest);
    }

}