package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.GeneralProjectInfoDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectValidateService {
    private final StageService stageService;
    private final TeamService teamService;

    public List<Stage> getStages(GeneralProjectInfoDto dto) {
        if (dto.getStagesIds() != null) {
            return stageService.findAllByIds(dto.getStagesIds());
        } else return null;
    }

    public List<Team> getTeams(GeneralProjectInfoDto dto) {
        if (dto.getTeamsIds() != null) {
            return teamService.findTeamsByIds(dto.getTeamsIds());
        } else return null;
    }

    public String getName(String projectName, CreateSubProjectDto dto) {
        if (dto.getName() == null) {
            return projectName;
        }
        if (!dto.getName().isEmpty() || !dto.getName().isBlank() && !dto.getName().matches(projectName)) {
            return dto.getName();
        } else return projectName;
    }

    public boolean dtoParentProjectIdDoesNotMatchProjectIdAndNotNull(Long projectId, CreateSubProjectDto dto) {
        return dto.getParentProjectId() != null && !dto.getParentProjectId().equals(projectId);
    }

    public boolean visibilityDtoAndProjectDoesNotMatches(ProjectVisibility visibility, CreateSubProjectDto dto) {
        return dto.getVisibility() != visibility;
    }

    public boolean statusDtoAndProjectDoesNotMatches(ProjectStatus status, CreateSubProjectDto dto) {
        return dto.getStatus() != status;
    }
}
