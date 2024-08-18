package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.subprojectfilter.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final SubProjectValidator subProjectValidator;
    private final SubProjectMapper subProjectMapper;
    private final MomentService momentService;
    private final ProjectValidator projectServiceValidator;
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final List<SubProjectFilter> subProjectFilters;
    private final TeamService teamService;
    private final List<ProjectFilter> filters;
    private final S3ServiceImpl s3Service;



    @Transactional
    public SubProjectDto createSubProject(SubProjectDto subProjectDto) {
        Project parentProject = getProjectById(subProjectDto.getParentProjectId());
        subProjectValidator.checkAllValidationsForCreateSubProject(subProjectDto, parentProject);
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProject.setParentProject(parentProject);
        Project save = projectRepository.save(subProject);
        return subProjectMapper.toDto(save);
    }

    @Transactional
    public void updateSubProject(SubProjectDto subProjectDto) {
        if (subProjectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            MomentDto momentDto = getMomentDto(subProjectDto);
            MomentDto savedMomentDto = momentService.addMoment(momentDto);
            subProjectDto.setMomentId(savedMomentDto.getId());
        }
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        projectRepository.save(subProject);
    }

    @Transactional
    public ProjectDto create(Long userId, ProjectDto projectDto) {
        projectServiceValidator.validateCreation(userId, projectDto);
        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(userId);
        project.setStatus(ProjectStatus.CREATED);
        if (project.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        projectServiceValidator.validateUpdating(projectDto);
        Project project = projectMapper.toEntity(projectDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findProjectsWithFilter(Long userId, ProjectFilterDto projectFilterDto) {
        List<ProjectFilter> applicableFilters = getApplicableProjectFilters(projectFilterDto);
        List<Project> filteredProjects = projectRepository.findAllAvailableProjectsByUserId(userId).toList();
        for (ProjectFilter applicableFilter : applicableFilters) {
            filteredProjects = applicableFilter.apply(filteredProjects.stream(), projectFilterDto).toList();
        }
        return projectMapper.toDtos(filteredProjects);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findAllProjects(Long userId) {
        return projectMapper.toDtos(projectRepository
                .findAllAvailableProjectsByUserId(userId)
                .toList());
    }

    @Transactional
    public List<SubProjectDto> getAllFilteredSubprojectsOfAProject(SubProjectFilterDto subProjectFilterDto,
                                                                   Long projectId) {
        Project parentProject = getProjectById(projectId);
        Stream<Project> projects = parentProject.getChildren().stream()
                .filter(project -> project.getVisibility().equals(parentProject.getVisibility()));
        return subProjectFilters.stream()
                .filter(filter -> filter.isApplicable(subProjectFilterDto))
                .reduce(projects, (stream, filter) -> filter.apply(stream, subProjectFilterDto), Stream::concat)
                .map(subProjectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto findById(Long userId, ProjectFilterDto projectFilterDto) {
        projectServiceValidator.validateProjectFilterDtoForFindById(projectFilterDto);
        return projectMapper.toDto(projectRepository
                .findAvailableByUserIdAndProjectId(userId, projectFilterDto.getIdPattern())
                .orElseThrow(() -> {
                    log.info("Project with id {} not found", projectFilterDto.getIdPattern());
                    return new DataValidationException("Project not found");
                }));
    }

    /**
     * Do not use this method in Controller. Only for internal use.
     */
    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    @Transactional
    public ProjectDto addImage(Long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        if (project.getMaxStorageSize().compareTo(newStorageSize) > 0) {
            Project simpleProject;
            simpleProject = s3Service.uploadImage(file, project);
            project.setCoverImageId(simpleProject.getCoverImageId());
            project.setStorageSize(newStorageSize);
        }
        return projectMapper.toDto(project);
    }

    @Transactional
    public void deleteImage(Long projectId) {
        Project project = getProjectById(projectId);
        project.setStorageSize(BigInteger.valueOf(1));
        s3Service.deleteImage(project.getCoverImageId());
    }

    @Transactional(readOnly = true)
    public ByteArrayOutputStream getImage(long projectId) {
        return s3Service.downloadImage(projectRepository.getProjectById(projectId).getCoverImageId());
    }


    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    private List<ProjectFilter> getApplicableProjectFilters(ProjectFilterDto projectFilterDto) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(projectFilterDto))
                .toList();
    }

    private MomentDto getMomentDto(SubProjectDto subProjectDto) {
        MomentDto momentDto = MomentDto.builder()
                .name("Выполнены все подпроекты")
                .projectIds(Collections.singletonList(subProjectDto.getChildrenId()))
                .build();
        List<TeamMember> teamMember = teamService.getAllTeamMember(subProjectDto.getId());
        List<Long> userIds = new ArrayList<>();
        for (TeamMember tm : teamMember) {
            userIds.add(tm.getUserId());
        }
        momentDto.setUserIds(userIds);
        return momentDto;
    }
}
