package faang.school.projectservice.service;

import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.filter.subprojectfilter.SubProjectFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final SubProjectValidator subProjectValidator;
    private final SubProjectMapper subProjectMapper;
    private final ProjectRepository projectRepository;
    private final List<SubProjectFilter> subProjectFilters;

    @Transactional
    public SubProjectDto createSubProject(SubProjectDto subProjectDto) {
        checkAllValidationsForCreateSubProject(subProjectDto);
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        Project parentProject = getProjectById(subProjectDto.getParentProjectId());
        subProject.setParentProject(parentProject);
        Project save = projectRepository.save(subProject);
        return subProjectMapper.toDto(save);
    }

    @Transactional
    public void updateSubProject(SubProjectDto subProjectDto) {
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(subProject);
        if (subProjectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            projectRepository.completeProjectSubprojects(subProjectDto.getId());
        }
        if (subProjectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            projectRepository.makeSubprojectsPrivate(subProjectDto.getId());
            subProject.setMoments(List.of(new Moment()));
        }
    }

    public List<SubProjectDto> getAllFilteredSubprojectsOfAProject(SubProjectFilterDto subProjectFilterDto,
                                                                   Long projectId) {
        Project parentProject = projectRepository.getProjectById(projectId);
        Stream<Project> projects = parentProject.getChildren().stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC));
        return subProjectFilters.stream()
                .filter(filter -> filter.isApplicable(subProjectFilterDto))
                .reduce(projects, (stream, filter) -> filter.apply(stream, subProjectFilterDto), Stream::concat)
                .map(subProjectMapper::toDto)
                .toList();
    }

    private void checkAllValidationsForCreateSubProject(SubProjectDto subProjectDto) {
        Long parentProjectId = subProjectDto.getParentProjectId();
        subProjectValidator.validateTheExistenceOfTheParenProject(parentProjectId);
        subProjectValidator.validateRootProjectHasNotParentProject(parentProjectId);
        subProjectValidator.validateSubProjectHasParentProject(parentProjectId);
        subProjectValidator.checkThePublicityOfTheProject(subProjectDto.getVisibility(), parentProjectId);
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
