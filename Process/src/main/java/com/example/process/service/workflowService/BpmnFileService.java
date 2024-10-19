package com.example.process.service.workflowService;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class BpmnFileService {
    public String loadBpmnFile(String processKey) {
        try {
            ClassPathResource resource = new ClassPathResource("WorkFlows/" + processKey + ".bpmn");
            InputStream inputStream = resource.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("BPMN file not found", e);
        }
    }
}
