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
import java.util.Objects;

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

        // TODO
        /* create tests
        remove entity ParentProject from ProjectDto and add parentProjectId
         */
//        validatorService.isParentProjectExists(projectDto.getParentProject().getId());
        List<Project> allChildren = projectRepository.findAll();
        List<Project> allSubProjects = allChildren.stream()
                .filter(project -> project.getParentProject() != null)
                .filter(project -> Objects.equals(project.getParentProject().getId(), projectDto.getId()))
                .toList();

        if (allSubProjects.isEmpty()) {
            return new ArrayList<>();
        }
        SubProjectFilterDto subProjectFilterDto = subProjectMapper.mapToProjectDto(projectDto);
        allSubProjects.forEach(project -> System.out.println(project.getName()));
        List<Project> allFilteredProjects = filters.stream()
                .filter(filter -> filter.isApplicable(subProjectFilterDto))
                .reduce(allSubProjects.stream(), (stream, filter) -> filter.apply(stream, subProjectFilterDto),
                        (s1, s2) -> s1)
                .toList();
        System.out.println("+_+_+_+_+_+_+_");
        allFilteredProjects.forEach(project -> System.out.println(project.getName() + " " + project.getId()));
        ProjectDto prdto = subProjectMapper.mapToProjectDto(allFilteredProjects.get(0));
        ProjectDto prdto1 = subProjectMapper.mapToProjectDto(allFilteredProjects.get(1));
        System.out.println("prdt:" + prdto.getName());
        System.out.println("prdt:" + prdto.getId());
        System.out.println("prdt:" + prdto.getStatus());
        System.out.println("prdt:" + prdto.getVisibility());
        System.out.println("prdt:" + prdto.getChildrenIds());
        System.out.println("prdt:" + prdto.getParentProject());
        System.out.println("prdtAllfields:"+prdto);
//        return allFilteredProjects.stream()
//                .map(subProjectMapper::mapToProjectDto)
//                .toList();
        return List.of(prdto1, prdto);
    }
}
