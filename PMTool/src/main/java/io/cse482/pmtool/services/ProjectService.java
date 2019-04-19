package io.cse482.pmtool.services;

import io.cse482.pmtool.domain.Project;
import io.cse482.pmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdateProject(Project project){

        //Logic

        return projectRepository.save(project);

    }
}
