package com.example.process.service.workflowService;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class BpmnFileService {
    public String loadBpmnFile(String processKey) {
        // Validate the processKey input to avoid invalid file paths
        if (processKey == null || processKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Process key must not be null or empty.");
        }

        // Construct the file path
        File file = new File("workflows/" + processKey.trim() + ".bpmn");

        try (InputStream inputStream = new FileInputStream(file)) {
            // Read file content into a byte array
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);

            // Convert to a String with UTF-8 encoding
            return new String(bdata, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Error reading BPMN file for processKey: " + processKey, e);
        }
    }

    public void saveBpmnFile(String processKey, String diagramXML) {
        try {
            // Define a path outside of the resources directory for runtime access
            String directoryPath = "Process/src/main/resources/WorkFlows";  // This creates a "workflows" folder in the project root
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
