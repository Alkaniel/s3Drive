package fr.enzogiardinelli.s3dback.services;

import fr.enzogiardinelli.s3dback.dto.FileMetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class S3Service {
    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "gif", "pdf", "txt", "docx", "xlsx", "zip", "mp4"
    );

    private void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        sanitizeFileName(originalFilename);

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Type de fichier non autorisé : " + extension);
        }

        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("Fichier trop volumineux. Maximum 50MB.");
        }
    }

    private void sanitizeFileName(String fileName) {
        if (fileName == null || fileName.contains("..") || fileName.contains("/")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
    }

    public List<FileMetaData> listFiles() {
        return s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build())
                .contents()
                .stream()
                .map(s3Object -> new FileMetaData(
                        s3Object.key(),
                        s3Object.size(),
                        s3Object.lastModified(),
                        ""
                ))
                .toList();
    }

    public void uploadFile(MultipartFile file) throws IOException {
        validateFile(file);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getOriginalFilename())
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
    }

    public ResponseEntity<byte[]> downloadFile(String fileName) {
        sanitizeFileName(fileName);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);

        String contentType = response.response().contentType();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(response.asByteArray());
    }

    public void deleteFile(String fileName) {
        sanitizeFileName(fileName);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(request);
    }
}