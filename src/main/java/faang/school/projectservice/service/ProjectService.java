package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ImageService imageService;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final UserContext userContext;
    private final List<ProjectFilter> projectFilters;

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        projectValidator.validateProjectIsUniqByIdAndName(project);
        project.setStatus(ProjectStatus.CREATED);
        if (Objects.isNull(projectDto.getOwnerId())) {
            project.setOwnerId(userContext.getUserId());
        }
        if(Objects.isNull(projectDto.getVisibility())) {
            project.setVisibility(ProjectVisibility.PRIVATE);
        }
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto update(@Min(1) Long projectId, ProjectDto projectDto) {
        projectValidator.exists(projectId);
        Project project = projectRepository.getProjectById(projectId);
        return projectMapper.toDto(
                projectRepository.save(projectMapper.updateEntity(projectDto, project)));
    }

    public Project findById(Long id) {
        return projectRepository.getProjectById(id);
    }

    @Transactional(readOnly = true)
    public ProjectDto findProjectById(@Min(1) Long id) {
        return projectMapper.toDto(findById(id));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getFilteredProject(ProjectFilterDto filters) {
        List<Project> projects = projectRepository.findAll().stream()
                .filter(project -> project.getTeams().stream().anyMatch(this::isTeamMember))
                .toList();

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projects.stream(), filters))
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProject() {
        return projectMapper.toDtos(projectRepository.findAll());
    }

    public void delete(@Min(1) Long projectId) {
        projectRepository.delete(projectId);
    }

    @Transactional
    public void uploadFile(MultipartFile file, @Min(1) Long projectId, String folder) {
        Project project = projectRepository.getProjectById(projectId);
        log.info("Image preprocessing");
        file = imageService.imageProcessing(file);
        String imageId = s3Service.uploadFile(file, folder);
        project.setCoverImageId(imageId);
        projectRepository.save(project);
    }

    private boolean isTeamMember(Team team) {
        Stream<TeamMember> teamMemberStream = team.getTeamMembers().stream();
        return teamMemberStream.anyMatch(teamMember ->
                teamMember.getUserId() == userContext.getUserId());
    }
}
