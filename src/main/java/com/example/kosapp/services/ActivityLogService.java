package com.example.kosapp.services;

import com.example.kosapp.models.ActivityLog;
import com.example.kosapp.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void log(String action) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setCreatedAt(LocalDate.now());
        activityLogRepository.save(log);
    }
}
