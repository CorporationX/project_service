package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.sub_project.SubProjectCreateDto;
import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.dto.sub_project.SubProjectUpdateDto;
import faang.school.projectservice.dto.sub_project.SubProjectViewDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.FilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ProjectServiceImp — реализация интерфейса сервиса подпроектов {@link SubProjectService}.
 *
 * @author Linempy
 * @since 21.07.2025
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SubProjectServiceImpl implements SubProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectMapper mapper;
    private final UserContext context;
    private final FilterService<Project, SubProjectFilterDto> filter;
    private final MomentRepository momentRepository;

    @Override
    @Transactional
    public SubProjectViewDto create(SubProjectCreateDto createDto) {
        Long currentCreatorId = context.getUserId();
        Project project = mapper.toEntity(createDto);

        Project parent = projectRepository.findById(createDto.parentId())
                .orElseThrow(() -> new NotFoundException("Родительский проект не найден"));

        validateCreationRules(project, parent);
        buildProject(project, parent, currentCreatorId);

        Project savedProject = projectRepository.save(project);
        log.info("Создан подпроект ID: {} для родительского ID {}", savedProject.getId(), parent.getId());
        return mapper.toViewDto(savedProject);
    }

    @Override
    @Transactional
    public SubProjectViewDto update(Long id, SubProjectUpdateDto updateDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Проект не найден"));

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new ForbiddenException("Проект уже завершен");
        }

        if (updateDto.status() == ProjectStatus.COMPLETED) {
            validateSubprojectsAreCompleted(project.getChildren());
            project.setStatus(updateDto.status());

            if (project.getParentProject() != null) {
                createCompletionMomentIfApplicable(project.getParentProject());
            }
        }

        if (updateDto.visibility() != project.getVisibility()) {
            checkVisibilityRules(project, updateDto);
            updateVisibilityRecursion(project, updateDto.visibility());
        }

        log.info("Подпроект id: {} был обновлен", project.getId());
        project = projectRepository.save(project);
        return mapper.toViewDto(project);
    }

    @Override
    public List<SubProjectViewDto> getByFilter(Long parentId, SubProjectFilterDto filterDto) {
        Project parentProject = projectRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Родительский проект ID: " + parentId + " не был найден"));
        List<Project> filteredDto = filter.getFilteredList(parentProject.getChildren(), filterDto);

        return filteredDto.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    private void validateCreationRules(Project project, Project parent) {
        checkVisibility(parent.getVisibility(), project.getVisibility());
        checkProjectStatusIsNotCompleted(parent);
    }

    private void checkProjectStatusIsNotCompleted(Project parent) {
        if (parent.getStatus() == ProjectStatus.COMPLETED) {
            throw new ForbiddenException("Нельзя создавать подпроекты для завершенного проекта");
        }
    }

    private void updateVisibilityRecursion(Project project, ProjectVisibility visibility) {
        if (project == null) {
            return;
        }

        project.setVisibility(visibility);
        log.info("Видимость проекта ID: {} была обновлена на {}", project.getId(), visibility);
        if (visibility == ProjectVisibility.PRIVATE && project.getChildren() != null) {
            project.getChildren().forEach(
                    child -> updateVisibilityRecursion(child, visibility)
            );
        }
    }

    private void checkVisibilityRules(Project project, SubProjectUpdateDto updateDto) {
        if (updateDto.visibility() == ProjectVisibility.PUBLIC
                && project.getParentProject() != null
                && project.getParentProject().getVisibility() == ProjectVisibility.PRIVATE) {
            throw new ForbiddenException("Нельзя сделать публичный подпроект приватного проекта");
        }
    }

    private void createCompletionMomentIfApplicable(Project parentProject) {
        if (parentProject.getChildren() == null || parentProject.getChildren().isEmpty()) {
            return;
        }

        boolean allProjectIsCompleted = getIncompleteProject(parentProject.getChildren()).isEmpty();
        if (!allProjectIsCompleted) {
            return;
        }

        Moment moment = buildCompletionMoment(parentProject);
        log.info("Создан момент завершения для проекта ID: {}. Завершены все {} подпроектов",
                parentProject.getId(), parentProject.getChildren().size());
        momentRepository.save(moment);

        if (parentProject.getMoments() == null) {
            parentProject.setMoments(new ArrayList<>());
        }
        parentProject.getMoments().add(moment);
    }

    private void validateSubprojectsAreCompleted(List<Project> children) {
        if (children == null || children.isEmpty()) {
            return;
        }

        List<String> incompleteProject = getIncompleteProject(children);

        if (!incompleteProject.isEmpty()) {
            throw new DataValidationException("Нельзя закрыть проект с незакрытыми подпроектами: "
                    + String.join(", ", incompleteProject));
        }
    }

    private List<String> getIncompleteProject(List<Project> children) {
        return children.stream()
                .filter(subProject -> subProject.getStatus() != ProjectStatus.COMPLETED)
                .map(Project::getName)
                .toList();
    }

    private Moment buildCompletionMoment(Project project) {
        return Moment.builder()
                .description("Выполнены все подпроекты!")
                .projects(List.of(project))
                .userIds(getAllParticipantIds(project))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<Long> getAllParticipantIds(Project project) {
        if (project.getTeams() == null) {
            return List.of();
        }

        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .distinct()
                .toList();
    }

    private void buildProject(Project project, Project parent, Long creatorId) {
        project.setParentProject(parent);
        project.setOwnerId(creatorId);
        project.setStatus(ProjectStatus.CREATED);
    }

    private void checkVisibility(ProjectVisibility parentVisibility, ProjectVisibility childVisibility) {
        if (parentVisibility == ProjectVisibility.PRIVATE
                && childVisibility == ProjectVisibility.PUBLIC) {
            throw new DataValidationException("Статус должен быть одинаковым у проекта-родителя и проекта-потомка");
        }
    }
}