package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StageServiceValidator {
    public void validateExecutorsStageRoles(StageDto stageDto) {
        Set<TeamRole> roles = stageDto.getStageRoles().stream()
                .map(StageRolesDto::role)
                .collect(Collectors.toSet());//роли в этапе

        for (TeamMemberDto dto : stageDto.getExecutorsDtos()) {
            if (isMemberHaveAnyStageRole(dto, roles)) {
                throw new IllegalArgumentException("executor id = " + dto.id()
                        + " excess in this stage");
            }
        }
    }

    private boolean isMemberHaveAnyStageRole(TeamMemberDto memberDto, Set<TeamRole> roles) {
        return memberDto.stageRoles().stream()
                .noneMatch(roles::contains);
    }

    public void validateProjectNotCanceled(ProjectStatus status) {
        if (status.equals(ProjectStatus.CANCELLED)) {
            throw new IllegalArgumentException("project was canceled");
        }
    }

    public void validateCount(Long count) {
        if (count == null || count < 0) {
            throw new IllegalArgumentException("incorrect reference of count");
        }
    }
}
