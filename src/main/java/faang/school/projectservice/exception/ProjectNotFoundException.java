package faang.school.projectservice.exception;


public class ProjectNotFoundException extends CustomException {

    public ProjectNotFoundException(Long id) {
        super(ExceptionMessage.PROJECT_NOT_FOUND, id);
    }
}
