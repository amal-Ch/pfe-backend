package com.example.workflow.services;

import com.example.workflow.DTO.TaskDto;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.exception.NullValueException;
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

    //public void startProcessWithVariables(String processKey, StartProcessInstanceDto dto) {
//        Map<String, VariableValueDto> variables = dto.getVariables();
//        runtimeService.startProcessInstanceByKey(processKey, variables.toString());
//    }
//        Map<String, VariableValueDto> variables = dto.getVariables();
//        String variablesJson = "";
//        try {
//            variablesJson = objectMapper.writeValueAsString(variables);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            // Handle exception
//        }
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, variablesJson);
//        return processInstance.getId();
//    }

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

        // Get process variables
//        public Object getVariable(String taskId, String variableName) {
//            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//            if (task == null) {
//                throw new RuntimeException("Task not found: " + taskId);
//            }
//            return taskService.getVariable(taskId, variableName);
//        }
//        public Map<String, Object> getProcessVariables (String processInstanceId){
//            return runtimeService.getVariables(processInstanceId);
//        }


        // Set process variables
//        public void setProcessVariables (String processInstanceId, Map < String, Object > variables){
//            runtimeService.setVariables(processInstanceId, variables);
//        }


//    public List<Task> getTasksForUser(String id) {
//        return processEngine.getTaskService().createTaskQuery()
//                .taskAssignee(id)
//                .list();
//    }
//

}



