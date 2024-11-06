package com.example.workflow.listeners;

import com.example.workflow.Controller.listenerController;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MyExecutionListener implements ExecutionListener {
    @Autowired
    listenerController listenerController;
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String currentActivityId = delegateExecution.getCurrentActivityId();
        System.out.println("Current Step: " + currentActivityId);

        String processId = delegateExecution.getProcessInstanceId();
        String activityId = delegateExecution.getCurrentActivityId();

        // Charger les statuts dynamiques à partir d'une configuration (fichier ou base de données)
        Integer statusId = loadStatusMap(processId);
        listenerController.notify( processId, statusId);

    }
    private Integer loadStatusMap(String processId) {
        return 1;
    }
}
