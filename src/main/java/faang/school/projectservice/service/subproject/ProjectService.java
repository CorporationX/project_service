package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.client.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.dto.client.subproject.SubProjectFilterDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.subproject.filters.SubProjectFilter;
import faang.school.projectservice.validator.subproject.ValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final SubProjectMapper subProjectMapper;
    private final ValidatorService validatorService;
    private final ProjectRepository projectRepository;
    private final List<SubProjectFilter> filters;

    public ProjectDto create(ProjectDto projectDto) {

        //TODO create tests
        CreateSubProjectDto subProjectDto = subProjectMapper.mapToSubDto(projectDto);
        subProjectDto.setStatus(ProjectStatus.CREATED);
        validatorService.isParentProjectExists(subProjectDto.getParentProject().getId());
        validatorService.isProjectExists(subProjectDto.getName());


        Project parentProject = projectRepository.findById(subProjectDto.getParentProject().getId());

        Project projectToSaveDb = subProjectMapper.mapToEntity(subProjectDto);
        projectToSaveDb.setParentProject(parentProject);
        Project result = projectRepository.save(projectToSaveDb);
//        projectRepository.save(projectToSaveDb);
//        return null;

        return subProjectMapper.mapToProjectDto(result);
    }

    public List<ProjectDto> getFilteredSubProjects(ProjectDto projectDto) {

        //TODO create tests
//        validatorService.isParentProjectExists(projectDto.getParentProject().getId());
        List<Project> allChildren = projectRepository.findAll();
        List<Project> allSubProjects = allChildren.stream()
                .filter(project -> project.getParentProject() != null)
                .filter(project -> project.getParentProject().getId() == projectDto.getId())
                .toList();

        if (allSubProjects.isEmpty()) {
            return new ArrayList<>();
        }
        SubProjectFilterDto subProjectFilterDto = subProjectMapper.mapToProjectDto(projectDto);
        System.out.println("===================");
        System.out.println(subProjectFilterDto);
        System.out.println("allSubProjects");
        allSubProjects.forEach(project -> System.out.println(project.getName()));
        List<Project> allFilteredProjects = filters.stream()
                .filter(filter -> filter.isApplicable(subProjectFilterDto))
                .peek(s -> System.out.println(s))
                .reduce(allSubProjects.stream(), (stream, filter) -> filter.apply(stream, subProjectFilterDto),
                        (s1, s2) -> s1)
                .toList();
        System.out.println("_+_+_+_+_+_+_+_");
        System.out.println(allFilteredProjects.size());
        allFilteredProjects.forEach(project -> System.out.println(project.getName()));

        List<ProjectDto> result = allFilteredProjects.stream()
                .map(subProjectMapper::mapToProjectDto)
                .toList();
        return result;
    }

}
