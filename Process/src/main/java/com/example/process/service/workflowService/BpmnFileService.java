package com.example.process.service.workflowService;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class BpmnFileService {
    public String loadBpmnFile(String processKey) {
        try {
            // Load from the external "workflows" directory
            File file = new File("workflows/" + processKey + ".bpmn");
            if (!file.exists()) {
                throw new FileNotFoundException("BPMN file not found for processKey: " + processKey);
            }

            // Read file content
            byte[] bdata = FileCopyUtils.copyToByteArray(new FileInputStream(file));
            return new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("BPMN file not found", e);
        }
    }

    public void saveBpmnFile(String processKey, String diagramXML) {
        try {
            // Define a path outside of the resources directory for runtime access
            String directoryPath = "workflows";  // This creates a "workflows" folder in the project root
            File directory = new File(directoryPath);

            // Ensure the directory exists
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Define the BPMN file path
            File bpmnFile = new File(directory, processKey + ".bpmn");

            // Write the XML content to the file
            try (FileOutputStream fos = new FileOutputStream(bpmnFile)) {
                fos.write(diagramXML.getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("BPMN file saved successfully at " + bpmnFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save BPMN diagram XML to file", e);
        }
    }

}
