package com.example.workflow.services;

import com.example.workflow.DTO.TaskDto;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.exception.NullValueException;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.rest.dto.runtime.StartProcessInstanceDto;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcessService {


    private static final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private FormService formService;

    public void startAndCompleteProcess(String processDefinitionKey) {
        // Start the process instance
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

        // Complete all tasks in sequence
        boolean completedAllTasks = false;
        while (!completedAllTasks) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    String taskId = task.getId();
                    String taskName = task.getName();

                    // Complete the task with necessary variables
                    Map<String, Object> variables = new HashMap<>();
                    if ("UserTask".equals(taskName)) {
                        // Simulate user input (replace with actual interaction logic)
                        variables.put("response", "bye"); // Example user input
                    }
                    taskService.complete(taskId, variables);
                }
            } else {
                // No more tasks found, break the loop
                completedAllTasks = true;
            }
            // Wait for a short duration to allow for asynchronous task creation
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public String  startProcess(String processDefinitionKey) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        return processInstance.getId();
    }

    public List<TaskDto> getTasks(String processInstanceId) {
        // Adjust the query to ensure only tasks that are currently active and assigned to the process instance are returned.
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (tasks.isEmpty()) {
            logger.warn("No active tasks found for process instance ID: {}", processInstanceId);
        }
        return tasks.stream()
                .map(task -> new TaskDto(task.getId(), task.getName()))
                .collect(Collectors.toList());
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        try {
            // Retrieve the task using the task ID
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

            if (task == null) {
                logger.error("Task with ID {} not found.", taskId);
                throw new RuntimeException("Task not found: " + taskId);
            }

            // Set the variables at the execution level (process instance level)
            if (variables != null && variables.containsKey("decision")) {
                Map<String, Object> processVariables = new HashMap<>();
                processVariables.put("decision", variables.get("decision"));
                runtimeService.setVariables(task.getExecutionId(), processVariables);
            }

            // Complete the task
            taskService.complete(taskId);
            logger.info("Task with ID {} completed successfully.", taskId);
        } catch (Exception e) {
            logger.error("Error completing task with ID {}: {}", taskId, e.getMessage());
            throw new RuntimeException("Error completing task: " + e.getMessage());
        }
    }

    public Map<String, Object> getTaskFormVariables(String taskId) {
        try {
            // Fetch the form data for the given task ID
            TaskFormData taskFormData = formService.getTaskFormData(taskId);

            // Prepare a map to hold form variables
            Map<String, Object> formVariables = new HashMap<>();

            // If form data is available, extract form variables
            if (taskFormData != null) {
                for (FormField field : taskFormData.getFormFields()) {
                    formVariables.put(field.getId(), field.getDefaultValue());
                }
            }

            return formVariables;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get form variables for task: " + e.getMessage(), e);
        }
    }

}



