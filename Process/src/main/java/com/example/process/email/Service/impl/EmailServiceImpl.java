package com.example.process.email.Service.impl;

import com.example.process.email.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


@Service
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Override
    public String sendMail(MultipartFile[] files, String to, String[] cc, String subject, String body, String requestData) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setCc(cc);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            // Attach additional files
            for (MultipartFile file : files) {
                mimeMessageHelper.addAttachment(file.getOriginalFilename(), new ByteArrayResource(file.getBytes()));
            }

            // Generate PDF from request data and attach it
//            mimeMessageHelper.addAttachment("RequestDetails.pdf", new InputStreamResource(pdfStream));

            javaMailSender.send(mimeMessage);
            return "Email sent successfully.";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String text, ByteArrayInputStream pdf) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            // Utilisation de MimeMessageHelper pour créer le message email avec pièce jointe
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);  // Destinataire
            helper.setSubject(subject);  // Objet de l'email
            helper.setText(text);  // Corps du message

            // Ajout de la pièce jointe (le PDF en pièce jointe)
            InputStreamSource attachment = new ByteArrayResource(pdf.readAllBytes());
            helper.addAttachment("generated.pdf", attachment);  // Nom de la pièce jointe

            // Envoi du message
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // Handle IOException from pdf.readAllBytes()
            System.err.println("Error reading PDF bytes: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
