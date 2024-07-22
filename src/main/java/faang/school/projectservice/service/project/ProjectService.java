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

import java.util.*;
import java.util.stream.Collectors;

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

    public CreateSubProjectDto updateProject(long id, CreateSubProjectDto dto,long userId) {
        Project project = projectJpaRepository.getReferenceById(id);
        List<Project> children = projectJpaRepository.getAllSubprojectsFor(id);
        List<Moment> moments = new ArrayList<>();
//        List<Project> children = projectJpaRepository.findAllById(childrenIds);
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
                List<Project> completedChildProjects = getCompletedChildProjects(children);
                if (!completedChildProjects.isEmpty() && completedChildProjects.size() == children.size()) {
                    project.setStatus(dto.getStatus());
                    moments = children.stream().
                            map(Project::getMoments).
                            flatMap(Collection::stream).
                            toList();
                    addMomentToList(id,moments,userId);
                    project.setMoments(moments);
                } else if (completedChildProjects.isEmpty()) {
                    project.setStatus(dto.getStatus());
                    addMomentToList(id,moments,userId);
                    project.setMoments(moments);
                } else {
                    throw new DataValidationException("current project has unfinished subprojects");
                }
            } else {
                project.setStatus(dto.getStatus());
            }
        }
        if (dto.getVisibility() != project.getVisibility()) {
            if (dto.getVisibility() == ProjectVisibility.PRIVATE) {
//                List<Long> childrenIds = projectJpaRepository.getAllSubprojectsIdsFor(id);
//                List<Project> children = projectJpaRepository.findAllById(childrenIds);
                project.setVisibility(dto.getVisibility());
                children.forEach(p -> p.setVisibility(dto.getVisibility()));
                projectJpaRepository.saveAll(children);
            } else {
                project.setVisibility(dto.getVisibility());
            }
        }
        checkDtoForNullFields(dto, project);


        return null;
    }

    private void addMomentToList(long id, List<Moment> moments, long userId){
        moments.add(Moment.builder()
                .name("project " + id + " has been completed")
                .updatedBy(userId)
                .createdBy(userId)
                .build());
    }

    private List<Project> getCompletedChildProjects(List<Project> childProjects) {
        if (!childProjects.isEmpty()) {
            return childProjects.stream().
                    filter(p -> p.getStatus().equals(ProjectStatus.COMPLETED)).
                    toList();
        }
        return List.of();
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
