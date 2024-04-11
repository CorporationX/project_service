package faang.school.projectservice.exception;

public class TeamMemberNotFoundException extends RuntimeException{
    public TeamMemberNotFoundException(Long userId, Long projectId){
        super(String.format(MessageError.TEAM_MEMBER_BY_USER_AND_PROJECT_IDS_NOT_FOUND_EXCEPTION.getMessage(), userId, projectId));
    }
    public TeamMemberNotFoundException(){
        super(MessageError.TEAM_MEMBER_NOT_FOUND.getMessage());
    }
}
