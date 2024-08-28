package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
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
    private final SubProjectMapper subProjectMapper;
    private final List<SubProjectFilter> subProjectFilters;
    private final CreateSubProjectDtoValidator validator;
    private final MomentRepository momentRepository;
    @Override
    public SubProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        validator.validateOnCreate(createSubProjectDto);
        Project subProject = subProjectMapper.toEntity(createSubProjectDto);
        Project parentProject =projectRepository.getProjectById(createSubProjectDto.getParentId());
        subProject.setParentProject(parentProject);
        parentProject.setChildren(new ArrayList<>());
        parentProject.getChildren().add(subProject);
        return subProjectMapper.toDTO(projectRepository.save(subProject));
    }

    @Override
    public SubProjectDto updateProject(UpdateSubProjectDto updateSubProjectDto) {
        validator.validateOnUpdate(updateSubProjectDto);
        Project project = subProjectMapper.toEntity(updateSubProjectDto);
        if(updateSubProjectDto.getVisibility().equals(ProjectVisibility.PRIVATE)){
            project.getChildren().forEach(x->x.setVisibility(ProjectVisibility.PRIVATE));
        }
        if(isAllSubProjectsCompleted(updateSubProjectDto)){
            createMomentForProject(project);

        }
        projectRepository.save(project);
        return subProjectMapper.toDTO(project);
    }

    @Override
    public List<SubProjectDto> getProjects(SubProjectDtoFilter filters, Long id) {
        Stream<Project> projects = projectRepository.getProjectById(id).getChildren().stream();

        return subProjectFilters.stream().filter(filter -> filter.isAcceptable(filters))
                .flatMap(filter -> filter.apply(projects, filters)).map(subProjectMapper::toDTO)
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
