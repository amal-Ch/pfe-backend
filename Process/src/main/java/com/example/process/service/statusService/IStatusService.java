package com.example.process.service.statusService;

import com.example.process.entity.Status;

import java.util.List;
import java.util.Map;

public interface IStatusService {
    List<Status> getAllStatuses();
    public Status createStatus(Map<String, Object> requestData);
    Status updateStatus(Integer idStatus, Status statusDetails);
    void deleteStatus(Integer idStatus);
}
