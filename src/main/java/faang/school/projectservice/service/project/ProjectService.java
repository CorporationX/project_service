package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.NoProjectsInTheDatabase;
import faang.school.projectservice.exception.ProjectAlreadyExistsException;
import faang.school.projectservice.exception.ProjectDoesNotExistInTheDatabase;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.projectservice.model.ProjectStatus.CREATED;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectDto projectDto){
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())){
            throw new ProjectAlreadyExistsException("Проект с именем " + projectDto.getName() + " уже существует");
        }
        Project project = projectMapper.toProject(projectDto);
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setStatus(CREATED);
        project = projectRepository.save(project);
        projectDto = projectMapper.toDto(project);
        return projectDto;
    }

    public ProjectDto updateProject(ProjectDto projectDto, ProjectStatus projectStatus, String description){
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())){
            throw new ProjectDoesNotExistInTheDatabase("Проект с именем " + projectDto.getName() + " не найден");
        }
        Project project = projectMapper.toProject(projectDto);
        project.setStatus(projectStatus);
        project.setDescription(description);
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);
        projectDto = projectMapper.toDto(project);
        return projectDto;
    }

    public List<ProjectDto> getAllProjects(){
        if (projectRepository.findAll() == null){
            throw new NoProjectsInTheDatabase("Список проектов пуст");
        }
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDto(projects);
    }

    public ProjectDto findProjectById(Long projectId){
        Project project = projectRepository.getProjectById(projectId);
        return projectMapper.toDto(project);
    }


}


//    public List<Project> getProjects(String name, ProjectStatus status) {
//        List<Project> projects = projectRepository.findAll();
//        if (name != null) {
//            projects = projects.stream()
//                    .filter(project -> project.getName().contains(name))
//                    .collect(Collectors.toList());
//        }
//        if (status != null) {
//            projects = projects.stream()
//                    .filter(project -> project.getStatus().equals(status))
//                    .collect(Collectors.toList());
//        }
//        return projects;
//    }

