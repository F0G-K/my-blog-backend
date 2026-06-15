package org.example.myblogbackend.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.myblogbackend.common.BusinessException;
import org.example.myblogbackend.config.MinioProperties;
import org.example.myblogbackend.dto.UploadVO;
import org.example.myblogbackend.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private static final long MAX_SIZE = 5L * 1024 * 1024;

    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
    public UploadVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只允许上传图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片不能超过 5MB");
        }

        String objectName = UUID.randomUUID().toString().replace("-", "") + extensionOf(file);
        try (InputStream in = file.getInputStream()) {
            ensureBucket();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectName)
                    .stream(in, file.getSize(), -1L)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new BusinessException("图片上传失败");
        }

        String url = properties.getEndpoint() + "/" + properties.getBucket() + "/" + objectName;
        return new UploadVO(url);
    }

    private void ensureBucket() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(properties.getBucket()).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
        }
    }

    private String extensionOf(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) {
            return name.substring(name.lastIndexOf('.')).toLowerCase();
        }
        return ".png";
    }
}
