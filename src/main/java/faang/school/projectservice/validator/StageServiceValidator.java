package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.exceptions.project.ProjectNotExistException;
import faang.school.projectservice.exceptions.stage.StageNotHaveProjectException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StageServiceValidator {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void validateStageDto(StageDto stageDto) {
        if (!projectRepository.existsById(stageDto.getProjectId())) {
            throw new StageNotHaveProjectException();
        }
        validateExecutorsStageRoles(stageDto);
    }

    public void validateExecutorsStageRoles(StageDto stageDto) {
        Set<TeamRole> roles = stageDto.getStageRoles().stream()
                .map(StageRolesDto::role).collect(Collectors.toSet());
        for (Long memberId : stageDto.getExecutorsIds()) {
            if (teamMemberRepository.findById(memberId).getRoles().stream()
                    .noneMatch(roles::contains)) {
                throw new IllegalArgumentException("executor id = " + memberId
                        + " excess in this stage");
            }
        }
    }

    public void validateProject(Long id) {
        if (projectRepository.existsById(id)) {
            throw new ProjectNotExistException();
        }

        if (projectRepository.getProjectById(id).getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new IllegalArgumentException("project id = " + id + " was canceled");
        }
    }
}
