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

    public CreateSubProjectDto updateProject(long id, CreateSubProjectDto dto, long userId) {
        Project project = projectJpaRepository.getReferenceById(id);
        List<Project> children = projectJpaRepository.getAllSubprojectsFor(id);
        List<Moment> moments = new ArrayList<>();
        validateSetGeneralFields(project, dto);
        updateStatus(project, dto, children, moments, userId);
        updateVisibility(project, dto, children);
        checkDtoForNullFields(dto, project);
        return subProjectMapper.toDto(projectJpaRepository.save(project));
    }

    private void validateSetGeneralFields(Project project, CreateSubProjectDto dto) {
        if (dto.getId() != null || !dto.getId().equals(project.getId())) {
            project.setId(dto.getId());
        }
        if (dto.getName()==null || dto.getName().isEmpty()){
            dto.setName(project.getName());
        }
        if (!dto.getName().isEmpty() ||  !dto.getName().isBlank() && !dto.getName().matches(project.getName())) {
            project.setName(dto.getName());
        } else {
            dto.setName(project.getName());
        }
        if (dto.getParentProjectId() != null && !dto.getParentProjectId().equals(project.getId())) {
            Project recentParentProject = project.getParentProject();
            Optional<Project> supposedParentProject = projectJpaRepository.findById(dto.getParentProjectId());

            supposedParentProject.ifPresentOrElse(p -> {
                        List<Project> supposedList = new ArrayList<>();
                        if (supposedParentProject.get().getChildren() == null) {
                            supposedList.add(project);
                            p.setChildren(supposedList);
                        } else {
                            supposedList.addAll(p.getChildren());
                            supposedList.add(project);
                            p.setChildren(supposedList);
                        }
                        if (recentParentProject != null) {
                            List<Project> recentList = new ArrayList<>(recentParentProject.getChildren());
                            recentList.remove(project);
                            recentParentProject.setChildren(recentList);
                        }
                    }
                    , () -> supposedParentProject.orElseThrow(() -> new DataValidationException("No such project in DB")));
        }
    }

    private void updateVisibility(Project project, CreateSubProjectDto dto, List<Project> children) {
        if (dto.getVisibility() != project.getVisibility()) {
            if (dto.getVisibility() == ProjectVisibility.PRIVATE) {
                project.setVisibility(dto.getVisibility());
                List<Project> completedChildProjects = getCompletedChildProjects(children);
                if (!completedChildProjects.isEmpty() && completedChildProjects.size() == children.size()) {
                    children.forEach(p -> p.setVisibility(dto.getVisibility()));
                    projectJpaRepository.saveAll(children);
                }
            } else {
                project.setVisibility(dto.getVisibility());
            }
        }
    }

    private void updateStatus(Project project, CreateSubProjectDto dto, List<Project> children, List<Moment> moments, Long userId) {
        if (dto.getStatus() != project.getStatus()) {
            if (dto.getStatus().equals(ProjectStatus.COMPLETED)) {
                List<Project> completedChildProjects = getCompletedChildProjects(children);
                if (!completedChildProjects.isEmpty() && completedChildProjects.size() == children.size()) {
                    project.setStatus(dto.getStatus());
                    List<Moment> childrenMoments = children.stream().
                            map(Project::getMoments)
                            .flatMap(Collection::stream)
                            .toList();
                    moments = addMomentToList(project.getId(), childrenMoments, userId);
                    project.setMoments(moments);
                } else if (completedChildProjects.isEmpty()) {
                    project.setStatus(dto.getStatus());
                    moments = addMomentToList(project.getId(), moments, userId);
                    project.setMoments(moments);
                } else {
                    throw new DataValidationException("current project has unfinished subprojects");
                }
            } else {
                project.setStatus(dto.getStatus());
            }
        }
    }

    private List<Moment> addMomentToList(long id, List<Moment> moments, long userId) {
        Moment moment = Moment.builder()
                .name("project " + id + " has been completed")
                .updatedBy(userId)
                .createdBy(userId)
                .build();
        List<Moment> list = new ArrayList<>(moments);
        list.add(moment);
        return list;
//        moments.add(moment);
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
