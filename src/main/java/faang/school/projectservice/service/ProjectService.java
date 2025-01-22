package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.redis.RedisTopicProperties;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMomentMapper;
import faang.school.projectservice.message.event.ProjectViewEvent;
import faang.school.projectservice.message.producer.impl.RedisMessagePublisher;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.utils.image.ImageUtils;
import faang.school.projectservice.validator.FileValidator;
import faang.school.projectservice.validator.ProjectValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMomentMapper projectMomentMapper;
    private final ProjectValidator projectValidator;
    private final List<SubProjectFilter> filters;
    private final UserContext userContext;
    private final RedisMessagePublisher redisMessagePublisher;
    private final RedisTopicProperties redisTopicProperties;
    private final UserServiceClient userServiceClient;

    private final StageService stageService;
    private final MomentService momentService;

    private final FileValidator fileValidator;
    private final S3Service s3Service;
    private final ImageUtils imageUtils;

    @Transactional
    public ProjectDto createSubProject(long parentProjectId,
                                       CreateSubProjectDto createDto) {
        log.info("Trying to create a sub project: {} for the project: {}",
                createDto, parentProjectId);
        Project parentProject = getProjectById(parentProjectId);
        projectValidator.validateCreateSubProject(parentProject, createDto);

        Project subProject = projectMapper.toEntity(createDto);
        subProject.setParentProject(parentProject);
        parentProject.addChildren(subProject);

        projectRepository.save(subProject);

        List<StageDto> stagesDto = createDto.stages();
        mapStages(subProject, stagesDto);

        log.info("Successfully created a sub project: {} for project: {}",
                createDto, parentProjectId);
        return projectMapper.toProjectDto(subProject);
    }

    @Transactional
    public ProjectDto updateSubProject(long projectId,
                                       UpdateSubProjectDto updateDto) {
        log.info("Trying to update a sub project: {} with the following parameters: {}",
                projectId, updateDto);
        Project subProject = getProjectById(projectId);
        projectValidator.validateUpdateSubProject(subProject, updateDto);

        projectMapper.update(updateDto, subProject);
        if (subProject.isPrivate() && subProject.hasChildren()) {
            subProject.setPrivateVisibility();
        }

        if (subProject.isCompleted()) {
            Moment moment = projectMomentMapper.toMoment(subProject);
            moment.addProject(subProject);
            subProject.addMoment(moment);
            momentService.createMoment(moment);
        }

        log.info("Successfully updated a sub project: {}", projectId);
        return projectMapper.toProjectDto(subProject);
    }

    @Transactional
    public List<ProjectDto> getFilteredSubProjects(long parentProjectId,
                                                   SubProjectFilterDto filterDto) {
        log.info("Trying to get sub projects for project: {} with the following filters: {}",
                parentProjectId, filterDto);
        Project parentProject = getProjectById(parentProjectId);

        if (parentProject.getChildren() == null) {
            log.info("Project: {} has no sub projects. Returning empty list", parentProjectId);
            return new ArrayList<>();
        }

        List<Project> subProjects = parentProject.getChildren();
        for (SubProjectFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                subProjects = filter.apply(subProjects, filterDto);
            }
        }

        log.info("Successfully got sub projects for project: {} with the following filters: {}",
                parentProjectId, filterDto);
        return projectMapper.toProjectDto(subProjects);
    }

    @Transactional
    public ResponseProjectDto addCover(long projectId, MultipartFile file) {
        log.info("Trying to add cover to project: {}", projectId);

        long maxAllowedSize = 5 * 1024 * 1024;
        fileValidator.validateFileSize(file, maxAllowedSize);
        fileValidator.validateFileIsImage(file);

        int maxWidth = 1080;
        int maxHeight = 566;
        BufferedImage image = imageUtils.getResizedBufferedImage(file, maxWidth, maxHeight);
        InputStream inputStream = imageUtils.getBufferedImageInputStream(file, image);

        String folder = "projectCovers";
        String coverImageId = s3Service.uploadFile(file, inputStream, folder);

        Project project = findProjectById(projectId);
        String oldCoverImageId = project.getCoverImageId();
        if (oldCoverImageId != null) {
            s3Service.deleteFile(oldCoverImageId);
        }

        project.setCoverImageId(coverImageId);

        return projectMapper.toResponseDto(project);
    }

    public ResponseProjectDto getProject(long projectId) {
        log.info("Trying to get project by id: {}", projectId);
        Project project = getProjectById(projectId);
        publishProjectProfileViewEvent(projectId);
        return projectMapper.toResponseDto(project);
    }

    public Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public Project findProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public boolean isProjectComplete(long id){
        return getProjectById(id).getStatus() == ProjectStatus.COMPLETED;
    }

    private void mapStages(Project subProject, List<StageDto> stagesDto) {
        if (stagesDto != null) {
            List<Stage> stages = stageService.getMappedStages(stagesDto);
            stages.forEach(stage -> {
                stage.setProject(subProject);
                stageService.createStage(stage);
            });
            subProject.setStages(stages);
        }
    }

    private void publishProjectProfileViewEvent(long projectId) {
        long userId = userContext.getUserId();
        validateUserExists(userId);

        log.info("Trying to publish project profile view event for project {} by user: {}", projectId, userId);
        ProjectViewEvent projectViewEvent = ProjectViewEvent.builder()
                .projectId(projectId)
                .userId(userId)
                .receivedAt(LocalDateTime.now())
                .build();

        redisMessagePublisher.publish(redisTopicProperties.getProjectViewChannel(), projectViewEvent);
    }

    private void validateUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with id %s does not exist".formatted(userId));
        }
    }
}