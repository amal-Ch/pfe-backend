package com.example.workflow.services;

import org.camunda.bpm.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Service
public class ProcessDeployer {
    @Autowired
    private RepositoryService repositoryService;
//@PostConstruct
//    public void DeployProcess() {
//        repositoryService.createDeployment()
//                .addClasspathResource("processes/simple_workflow.bpmn")
//                .deploy();
//    }


    public void deployProcess(InputStream bpmnFile, String deploymentName) {
        repositoryService.createDeployment()
                .addInputStream(deploymentName, bpmnFile)
                .name(deploymentName)
                .deploy();
    }

//    public void DeployProcess(){
//        repositoryService.createDeployment().addClasspathResource("processes/simple_workflow.bpmn").deploy();
//    }
}
