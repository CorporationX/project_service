package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@RequiredArgsConstructor
@Validated
public class SubProjectServiceValidate {
    private final TeamRepository teamRepository;
    private final StageJpaRepository stageRepository;

    public List<Team> getTeams(@Valid CreateSubProjectDto dto) {
        if (dto.getTeamsIds() == null) {
            return null;
        }
        return teamRepository.findAllById(dto.getTeamsIds());
    }

    public List<Stage> getStages(@Valid CreateSubProjectDto dto) {
        if (dto.getStagesIds() == null) {
            return null;
        }
        return stageRepository.findAllById(dto.getStagesIds());
    }

    public boolean isVisibilityDtoAndProjectNotEquals(@Valid CreateSubProjectDto dto, @Valid Project project) {
        return dto.getVisibility() != project.getVisibility();
    }

    public boolean isStatusDtoAndProjectNotEquals(@Valid CreateSubProjectDto dto,@Valid Project project) {
        return dto.getStatus() != project.getStatus();
    }
}
