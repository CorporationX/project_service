package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class SubProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final MomentService momentService;
    private final SubProjectServiceValidate projectValidator;
    private final List<SubProjectFilter<FilterSubProjectDto, Project>> subProjectFilters;

    @Transactional
    public CreateSubProjectDto createSubProject(@NotNull @Positive Long parentId,
                                                @Valid CreateSubProjectDto subProjectDto) {
        Project parentProject = projectRepository.getProjectById(parentId);
        Project childProject = subProjectMapper.toEntity(subProjectDto);
        validateVisibilityProjectAndSubProject(parentProject, childProject);

        log.info("Got child project from Dto withing creating subproject with id = {}", childProject.getId());
        childProject.setParentProject(parentProject);
        parentProject.getChildren().add(childProject);
        parentProject.setStages(projectValidator.getStages(subProjectDto));
        parentProject.setTeams(projectValidator.getTeams(subProjectDto));
        projectRepository.save(parentProject);
        projectRepository.save(childProject);
        log.info("Saved child project with id = {} to DB", childProject.getId());
        return subProjectMapper.toDto(childProject);
    }

    public CreateSubProjectDto updateProject(@NotNull @Positive Long projectId,
                                             @Valid CreateSubProjectDto dto,
                                             @NotNull @Positive Long userId) {
        Project project = projectRepository.getProjectById(projectId);
        List<Project> children = projectRepository.getSubProjectsByParentId(projectId);
        if (projectValidator.isVisibilityDtoAndProjectNotEquals(dto, project)) {
            updateVisibility(project, dto, children);
        }
        if (projectValidator.isStatusDtoAndProjectNotEquals(dto, project)) {
            updateStatus(project, dto, children, userId);
        }
        project.setStages(projectValidator.getStages(dto));
        project.setTeams(projectValidator.getTeams(dto));
        return subProjectMapper.toDto(projectRepository.save(project));
    }

    public List<CreateSubProjectDto> getProjectsByFilter(@NotNull @Positive Long projectId,
                                                         FilterSubProjectDto filterDto) {
        Stream<Project> projectStream = projectRepository.getSubProjectsByParentId(projectId).stream();
        return subProjectFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(projectStream, filterDto))
                .distinct()
                .map(subProjectMapper::toDto)
                .toList();
    }

    private void updateStatus(Project project, CreateSubProjectDto dto, List<Project> children, Long userId) {
        log.info("Trying to update status of project with id = {}", project.getId());
        if (dto.getStatus() != ProjectStatus.COMPLETED) {
            project.setStatus(dto.getStatus());
        } else {
            log.info("Trying to complete project with id = {}", project.getId());
            List<Project> completedProjects = children.stream()
                    .filter(child -> child.getStatus().equals(ProjectStatus.COMPLETED))
                    .toList();
            log.info("Got {} completed projects", completedProjects.size());
            if (!completedProjects.isEmpty() && completedProjects.size() != children.size()) {
                log.error("Can't complete project because not all children are completed");
                throw new DataValidationException("Current project has unfinished subprojects");
            }
            project.setStatus(dto.getStatus());
            addMoment(project.getId(), completedProjects, userId);
        }
        log.info("Set project status to {}", dto.getStatus());
    }

    private void addMoment(Long id, List<Project> completedChildProjects, Long userId) {
        log.info("Trying to create moment for project with id = {}", id);
        String message = "Project with id = " + id + " is completed";
        MomentRequestDto momentRequestDto = new MomentRequestDto();
        momentRequestDto.setName(message);
        List<Long> completedProjectsIds = completedChildProjects.stream().map(Project::getId).toList();
        List<Long> completedProjects = new ArrayList<>();
        completedProjects.add(id);
        completedProjects.addAll(completedProjectsIds);
        momentRequestDto.setProjectIds(completedProjects);
        log.info("Created moment for project with id = {}", id);
        momentService.create(momentRequestDto, userId);
    }

    private void updateVisibility(Project project, CreateSubProjectDto dto, List<Project> children) {
        log.info("Trying to update visibility of project with id = {}", project.getId());
        project.setVisibility(dto.getVisibility());
        log.info("Set project visibility to {} for project with id = {}",
                dto.getVisibility(), project.getId()
        );
        if (dto.getVisibility().equals(ProjectVisibility.PRIVATE) && !children.isEmpty()) {
            children.forEach(child -> child.setVisibility(dto.getVisibility()));
            projectRepository.saveAll(children);
            log.info("Set project visibility to {} for all children of project with id = {}",
                    dto.getVisibility(), project.getId()
            );
        }
    }

    private void validateVisibilityProjectAndSubProject(Project parentProject, Project childProject) {
        if (parentProject.getVisibility() == ProjectVisibility.PUBLIC &&
                childProject.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            log.warn("Not allowed to create private sub project in public project." +
                            " Parent project id {} is {} and sub project id {} is {}",
                    parentProject.getId(),
                    parentProject.getVisibility(),
                    childProject.getId(),
                    childProject.getVisibility()
            );
            throw new DataValidationException("Sub project can't be private in public project.");
        }
    }


}