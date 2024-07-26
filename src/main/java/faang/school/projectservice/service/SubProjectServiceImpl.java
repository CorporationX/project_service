package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.CreateSubProjectDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubProjectServiceImpl implements SubProjectService{
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<SubProjectFilter> subProjectFilters;
    private final CreateSubProjectDtoValidator validator;
    private final MomentRepository momentRepository;
    @Override
    public ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        validator.validateOnCreate(createSubProjectDto);
        Project subProject = projectMapper.toEntity(createSubProjectDto);
        Project parentProject =projectRepository.getProjectById(createSubProjectDto.getParentId());
        subProject.setParentProject(parentProject);
        parentProject.setChildren(new ArrayList<>());
        parentProject.getChildren().add(subProject);
        return projectMapper.toDTO(projectRepository.save(subProject));
    }

    @Override
    public ProjectDto updateProject(UpdateSubProjectDto updateSubProjectDto) {
        validator.validateOnUpdate(updateSubProjectDto);
        Project project =projectMapper.toEntity(updateSubProjectDto);
        if(updateSubProjectDto.getVisibility().equals(ProjectVisibility.PRIVATE)){
            project.getChildren().forEach(x->x.setVisibility(ProjectVisibility.PRIVATE));
        }
        if(isAllSubProjectsCompleted(updateSubProjectDto)){
            createMomentForProject(project);

        }
        projectRepository.save(project);
        return projectMapper.toDTO(project);
    }

    @Override
    public List<ProjectDto> getProjects(SubProjectDtoFilter filters, Long id) {
        Stream<Project> projects = projectRepository.getProjectById(id).getChildren().stream();

        return subProjectFilters.stream().filter(filter -> filter.isAcceptable(filters))
                .flatMap(filter -> filter.apply(projects, filters)).map(projectMapper::toDTO)
                .toList();
    }

    public boolean isAllSubProjectsCompleted(UpdateSubProjectDto updateSubProjectDto) {
        List<Project> projects = projectRepository.getProjectById(updateSubProjectDto.getId()).getChildren();
        for (Project project : projects) {
            if(project.getStatus()!= ProjectStatus.COMPLETED){
                return false;
            }
        }
        return true;
    }
    public void createMomentForProject(Project project) {
        Moment moment = new Moment();
        moment.setName("Выполнены все подпроекты");
        moment.setUserIds(project.getTeams().stream().flatMap(x->{
            List<Long> userIds = x.getTeamMembers().stream().map(TeamMember::getUserId).toList();
            return userIds.stream();
        }).toList());
        momentRepository.save(moment);
    }
}
