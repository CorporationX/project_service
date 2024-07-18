package faang.school.projectservice.validation.team_member;

public interface TeamMemberValidator {
    void validateExistence(long id);

    void validateExistenceByUserIdAndProjectId(long userId, long projectId);
}
