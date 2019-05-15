package io.cse482.pmtool.services;

import io.cse482.pmtool.domain.Backlog;
import io.cse482.pmtool.domain.ProjectTask;
import io.cse482.pmtool.repositories.BacklogRepository;
import io.cse482.pmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sound.midi.Sequence;

@Service
public class ProjectTaskService {


    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;


    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask){

        //Exception: project not found

        //all project tasks to be added to a specific project, project != null, backlog exists
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
        //set backlog to project task
        projectTask.setBacklog(backlog);

        //want project sequence to be like this: IDPRO-1 IDPRO-2
        Integer BacklogSequence = backlog.getPTSequence();
        //update backlog sequence
        BacklogSequence++;

        backlog.setPTSequence(BacklogSequence);

        //add sequence to project task
        projectTask.setProjectSequence(projectIdentifier+"-"+BacklogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);


        //initial status when status is null
        if(projectTask.getStatus()==""||projectTask.getStatus()==null){
            projectTask.setStatus("TO_DO");
        }

        //Initial priority when priority is null
        if(projectTask.getPriority()==null){
                projectTask.setPriority(3);
        }

        return projectTaskRepository.save(projectTask);


    }

    public Iterable<ProjectTask>findBacklogById(String id){
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }


}
