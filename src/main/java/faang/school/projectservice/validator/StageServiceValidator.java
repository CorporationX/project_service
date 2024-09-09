package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.exceptions.project.ProjectNotExistException;
import faang.school.projectservice.exceptions.stage.StageNotHaveProjectException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StageServiceValidator {
    private final ProjectRepository projectRepository;

    public void validateStageDto(StageDto stageDto) {
        if (!projectRepository.existsById(stageDto.getProjectId())) {
            throw new StageNotHaveProjectException();
        }
        validateExecutorsStageRoles(stageDto);
    }

    public void validateExecutorsStageRoles(StageDto stageDto) {
        Set<TeamRole> roles = stageDto.getStageRoles().stream()
                .map(StageRolesDto::role).collect(Collectors.toSet());//роли в этапе

        for (TeamMemberDto dto : stageDto.getExecutorsDtos()) {
            if (dto.stageRoles().stream()
                    .anyMatch(roles::contains)) {
                throw new IllegalArgumentException("executor id = " + dto.id()
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

    public void validateCount(Long count) {
        if (count == null || count < 0) {
            throw new IllegalArgumentException("incorrect reference of count");
        }
    }
}
