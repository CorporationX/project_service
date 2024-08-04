package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.filter.subprojectfilter.SubProjectFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final SubProjectValidator subProjectValidator;
    private final SubProjectMapper subProjectMapper;
    private final MomentService momentService;
    private final ProjectRepository projectRepository;
    private final List<SubProjectFilter> subProjectFilters;

    @Transactional
    public SubProjectDto createSubProject(SubProjectDto subProjectDto) {
        Project project = getProjectById(subProjectDto.getId());
        subProjectValidator.checkAllValidationsForCreateSubProject(subProjectDto, project);
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        Project parentProject = getProjectById(subProjectDto.getParentProjectId());
        subProject.setParentProject(parentProject);
        Project save = projectRepository.save(subProject);
        return subProjectMapper.toDto(save);
    }

    @Transactional
    public void updateSubProject(SubProjectDto subProjectDto) {
        MomentDto momentDto = new MomentDto();
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        projectRepository.save(subProject);
        if (subProjectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            projectRepository.completeProjectSubprojects(subProjectDto.getId());
        }
        if (subProjectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            projectRepository.makeSubprojectsPrivate(subProjectDto.getId());
            momentService.addMoment(momentDto);
        }
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
    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
