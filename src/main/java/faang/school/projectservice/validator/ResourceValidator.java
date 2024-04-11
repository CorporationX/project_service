package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.TeamMemberNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceValidator {
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public void validateForOwner(long userId, Resource resource) {
        TeamMember teamMember = validateForTeamMemberExistence(userId, resource.getProject().getId());
        long teamMemberId = teamMember.getId();

        long resourceAuthorId = resource.getCreatedBy().getId();
        long projectOwnerId = resource.getProject().getOwnerId();
        if (projectOwnerId != teamMemberId && resourceAuthorId != teamMemberId) {
            throw new DataValidationException("Only author of the file or project owner can delete it");
        }
    }

    public TeamMember validateForTeamMemberExistence(long userId, long projectId) {
        TeamMember author = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (author == null) {
            throw new TeamMemberNotFoundException(userId, projectId);
        }
        return author;
    }
}
