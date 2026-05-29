package fr.enzogiardinelli.s3dback.controller;

import fr.enzogiardinelli.s3dback.dto.FileMetaData;
import fr.enzogiardinelli.s3dback.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/files")
    public ResponseEntity<List<FileMetaData>> listFiles() {
        return ResponseEntity.ok(s3Service.listFiles());
    }

    @PostMapping("/files/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam("file")MultipartFile file) throws IOException {
        s3Service.uploadFile(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/files/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName) throws IOException {
        return s3Service.downloadFile(fileName);
    }

    @DeleteMapping("/files/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable("fileName") String fileName) {
        s3Service.deleteFile(fileName);
        return ResponseEntity.ok().build();
    }
}
