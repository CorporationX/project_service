package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.publisher.ProjectViewEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.converter.GigabyteConverter;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private static final int POWER_OF_THREE = 3;
    private static final int GIGABYTE_MULTIPLIER_TWO = 2;
    private static final int THOUSAND_BYTES = 1000;
    private static final BigInteger STORAGE_SIZE = new BigInteger(String.
            valueOf(Math.round(Math.pow(THOUSAND_BYTES, POWER_OF_THREE) * GIGABYTE_MULTIPLIER_TWO)));

    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;
    private final List<Filter<ProjectFilterDto, Project>> projectFilters;
    private final ProjectMapper projectMapper;
    private final GigabyteConverter gigabyteConverter;
    private final UserContext userContext;
    private final ProjectViewEventPublisher projectViewEventPublisher;

    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.validateOwnerHasSameProject(projectDto);

        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setCreatedAt(LocalDateTime.now());

        Project createdProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(createdProject);
    }

    public ProjectDto update(ProjectDto projectDto) {

        Project existedProject = projectRepository.findById(projectDto.getId()).orElseThrow(() ->
                new EntityNotFoundException("Project with id" + projectDto.getId() + "does not exist"));

        existedProject.setDescription(projectDto.getDescription());
        existedProject.setStatus(projectDto.getStatus());
        existedProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(existedProject);

        return projectMapper.toDto(existedProject);
    }

    public List<ProjectDto> getProjectByNameAndStatus(ProjectFilterDto projectFilterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(projectFilterDto))
                .flatMap(filter -> filter.applyFilter(projects, projectFilterDto))
                .map(projectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getAllProjectDto() {
        return projectMapper.toDtos(projectRepository.findAll());
    }

    public ProjectDto getProject(Long id) {
        log.info("getProject() - start");
        Project project = getProjectById(id);
        long contextUserId = userContext.getUserId();

        CompletableFuture.runAsync(() -> {
            ProjectViewEvent projectViewEvent = ProjectViewEvent.builder()
                    .projectId(project.getId())
                    .userId(contextUserId)
                    .viewTime(LocalDateTime.now())
                    .build();
            log.debug("Trying to send projectViewEvent - {}", projectViewEvent);
            projectViewEventPublisher.publish(projectViewEvent);
        });

        log.info("getProject() - finish");
        return projectMapper.toDto(project);
    }

    public List<Project> getProjectByIds(List<Long> ids) {
        return projectRepository.findAllById(ids);
    }

    public Project getProjectById(long id) {
        return projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Project with id " + id + "does not exist!"));
    }

    public void setNewProjectStorageSize(Project project) {
        if (project.getStorageSize() == null) {
            project.setStorageSize(STORAGE_SIZE);
            log.debug("Set project {} storage for {} GB", project.getName(),
                    gigabyteConverter.byteToGigabyteConverter(STORAGE_SIZE.longValue()));
        }
    }
}
