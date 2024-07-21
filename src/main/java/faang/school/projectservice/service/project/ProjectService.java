package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectValidator;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;
    private final StageRepository stageRepository;
    private final TeamRepository teamRepository;
    private final MomentRepository momentRepository;
    private final ProjectMapper projectMapper;
    private final SubProjectMapper subProjectMapper;

    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        checkDtoForNullFields(projectDto, project);
        return projectMapper.toDto(projectJpaRepository.save(project));
    }

    public CreateSubProjectDto createSubProject(Long parentProjectId, CreateSubProjectDto subProjectDto) {
        Project parentProject = projectJpaRepository.getReferenceById(parentProjectId);
        Project childProject = subProjectMapper.toEntity(subProjectDto);
        childProject.setParentProject(parentProject);
        if (parentProject.getId() != null) {
            parentProject.getChildren().add(childProject);
            checkDtoForNullFields(subProjectDto, parentProject);
            if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) &&
                    childProject.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                log.warn("It's not possible to a create private subproject for a public project");
                throw new DataValidationException("It's not possible to a create private subproject for a public project");
            }
        }
        projectJpaRepository.save(parentProject);
        return subProjectMapper.toDto(projectJpaRepository.save(childProject));
    }

    public CreateSubProjectDto updateProject(long id, CreateSubProjectDto dto) {
        Project project = projectJpaRepository.getReferenceById(id);
        if (dto.getId() != null) {
            project.setId(dto.getId());
        }
        if (!dto.getName().isEmpty() || !dto.getName().isBlank()) {
            project.setName(dto.getName());
        }
        if (dto.getParentProjectId() != null) {
            Optional<Project> parentProject = projectJpaRepository.findById(dto.getParentProjectId());
            parentProject.ifPresent(project::setParentProject);
        }
        if (dto.getChildrenIds() != null) {
            Optional<List<Project>> childProject = Optional.of(projectJpaRepository.findAllById(dto.getChildrenIds()));
            childProject.ifPresent(project::setChildren);
        }
        if (dto.getStatus() != project.getStatus()) {
            if (dto.getStatus().equals(ProjectStatus.COMPLETED)) {
                if (project.getMoments() != null || dto.getMomentsIds() != null) {
                    project.setStatus(dto.getStatus());
                } else {
                    throw new DataValidationException("current project has unfinished subprojects");
                }
            } else {
                project.setStatus(dto.getStatus());
            }
        }
        // if set private, then you should set up private visibility for each child project
        if (dto.getVisibility()!=project.getVisibility()){
            project.setVisibility(dto.getVisibility());
        }
        checkDtoForNullFields(dto,project);
        TreeSet<Project> childrenSet = new TreeSet<>();


        return null;
    }

    private void checkDtoForNullFields(ProjectValidator dto, Project project) {
        if (dto.getStagesIds() != null) {
            List<Stage> stageList = stageRepository.findAllById(dto.getStagesIds());
            project.setStages(stageList);
        }
        if (dto.getTeamsIds() != null) {
            List<Team> teamList = teamRepository.findAllById(dto.getTeamsIds());
            project.setTeams(teamList);
        }
    }

}
