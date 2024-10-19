package com.example.process.email.Controller;

import com.example.process.email.Service.EmailService;
import com.example.process.email.Service.impl.PdfGeneratorService;
import com.example.process.entity.Request;
import com.example.process.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/mail")
public class EmailSendController {
    private EmailService emailService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private RequestRepository requestRepository;

   /* @GetMapping("/generate/{requestId}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Integer requestId) {
        // Find the request by ID
        Optional<Request> requestOpt = requestRepository.findById(requestId);

        if (!requestOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Generate the PDF
        Request request = requestOpt.get();
        ByteArrayInputStream pdfStream = pdfGeneratorService.generatePdfForRequest(request);

        // Convert the ByteArrayInputStream to a byte array
        byte[] pdfBytes = pdfStream.readAllBytes();

        // Set headers to download the file
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "request-details.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
*/
    public EmailSendController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendMail(
            @RequestParam(value = "file", required = false) MultipartFile[] file,
            @RequestParam String to,
            @RequestParam String[] cc,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam String requestData) {
        return emailService.sendMail(file, to, cc, subject, body, requestData);
    }
}
