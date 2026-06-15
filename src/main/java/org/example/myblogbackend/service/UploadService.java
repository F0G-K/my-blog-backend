package org.example.myblogbackend.service;

import org.example.myblogbackend.dto.UploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    UploadVO upload(MultipartFile file);
}
