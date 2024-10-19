package com.example.process.email.Service;

import org.springframework.web.multipart.MultipartFile;

public interface EmailService {
     String sendMail(MultipartFile[] files, String to, String[] cc, String subject, String body, String requestData);
}
