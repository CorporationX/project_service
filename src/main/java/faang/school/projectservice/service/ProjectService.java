package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.filter.subprojectfilter.SubProjectFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final TeamService teamService;

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
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProject.setStatus(subProjectDto.getStatus());
        subProject.setVisibility(subProjectDto.getVisibility());
        projectRepository.save(subProject);
        MomentDto momentDto = getMomentDto(subProject);
        momentService.addMoment(momentDto);

    }

    private MomentDto getMomentDto(Project subProject) {
        MomentDto momentDto = new MomentDto();
        momentDto.setName("Выполнены все подпроекты");
        momentDto.setProjectIds(Arrays.asList(subProject.getId()));
        List<TeamMember> teamMember = teamService.getAllTeamMember(subProject.getId());
        List<Long> userIds = new ArrayList<>();
        for (TeamMember tm : teamMember) {
            userIds.add(tm.getUserId());
        }
        momentDto.setUserIds(userIds);
        return momentDto;
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
