package io.cse482.pmtool.services;

import io.cse482.pmtool.domain.Backlog;
import io.cse482.pmtool.domain.Project;
import io.cse482.pmtool.domain.ProjectTask;
import io.cse482.pmtool.exceptions.ProjectNotFoundException;
import io.cse482.pmtool.repositories.BacklogRepository;
import io.cse482.pmtool.repositories.ProjectRepository;
import io.cse482.pmtool.repositories.ProjectTaskRepository;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sound.midi.Sequence;
import java.util.List;

@Service
public class ProjectTaskService {


    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;


    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask){

        //Exception: project not found

        try {
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
        }catch (Exception e){
            throw new ProjectNotFoundException("Project not found.");
        }

    }

    public Iterable<ProjectTask>findBacklogById(String id){

        Project project = projectRepository.findByProjectIdentifier(id);

        if(project==null){
            throw new ProjectNotFoundException("Project with ID: '"+id+"' does not exist");
        }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id){

        //make sure we are searching on an existing backlog
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if(backlog==null){
            throw new ProjectNotFoundException("Project with ID: '"+backlog_id+"' does not exist");
        }

        //make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);

        if(projectTask == null){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' not found");
        }

        //make sure that the backlog/project id in the path corresponds to the right project
        if(!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' does not exist in project: '"+backlog_id);
        }


        return projectTask;
    }

    //Update project task
    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id){
        //find existing project task
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);

        //replace it with updated project
        projectTask = updatedTask;

        //save update
        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);

        projectTaskRepository.delete(projectTask);
    }


}
