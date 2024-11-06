package com.example.process.Controller;

import com.example.process.entity.Status;
import com.example.process.service.statusService.IStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    @Autowired
    private IStatusService statusService;

    // Get all statuses
    @GetMapping("/Allstatuses")
    public List<Status> getAllStatuses() {
        return statusService.getAllStatuses();
    }


    // Create a new status
    @PostMapping("/Addstatus")
    public ResponseEntity<Status> createStatus(@RequestBody Map<String, Object> requestData) {
        Status newStatus = statusService.createStatus(requestData);
        return ResponseEntity.ok(newStatus);
    }

    // Update an existing status
    @PutMapping("UpdateStatus/{idStatus}")
    public ResponseEntity<Status> updateStatus(@PathVariable Integer idStatus, @RequestBody Status statusDetails) {
        try {
            return ResponseEntity.ok(statusService.updateStatus(idStatus, statusDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a status
    @DeleteMapping("DeleteStatus/{idStatus}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Integer idStatus) {
        statusService.deleteStatus(idStatus);
        return ResponseEntity.noContent().build();
    }
}
