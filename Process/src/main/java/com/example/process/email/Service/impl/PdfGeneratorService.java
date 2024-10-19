package com.example.process.email.Service.impl;

import com.example.process.entity.Request;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class PdfGeneratorService {

    public ByteArrayInputStream generatePdfForRequest(Request request) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add PDF content
            document.add(new Paragraph("Request Details"));
            document.add(new Paragraph("Full Name: " + (request.getFullName() != null ? request.getFullName() : "N/A")));
            document.add(new Paragraph("Object: " + (request.getObject() != null ? request.getObject() : "N/A")));
            document.add(new Paragraph("Added Date: " + (request.getAddedDateRequest() != null ? request.getAddedDateRequest().toString() : "N/A")));
            document.add(new Paragraph("Process Instance ID: " + (request.getProcessInstanceId() != null ? request.getProcessInstanceId() : "N/A")));

            document.close();

            // Save the PDF locally to check it
            try (FileOutputStream fos = new FileOutputStream("request-details.pdf")) {
                fos.write(out.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
