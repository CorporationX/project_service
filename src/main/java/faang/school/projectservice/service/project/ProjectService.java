package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;
    private final StageRepository stageRepository;
    private final TeamRepository teamRepository;
    private final ProjectMapper projectMapper;
    public ProjectDto createProject(ProjectDto projectDto){
        Project project = projectMapper.toEntity(projectDto);
        if (projectDto.getParentProjectId()!=null) {
            List<Project> childrenProjects = projectJpaRepository.findAllById(projectDto.getChildrenIds());
            project.setChildren(childrenProjects);
        }
        if (projectDto.getStagesIds()!=null){
            List<Stage> stages = stageRepository.findAllById(projectDto.getStagesIds());
            project.setStages(stages);
        }
        if (projectDto.getTeamsIds()!=null){
            List<Team> teams = teamRepository.findAllById(projectDto.getTeamsIds());
            project.setTeams(teams);
        }
        return projectMapper.toDto(projectJpaRepository.save(project));
    }
}
