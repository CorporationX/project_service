package faang.school.projectservice.exception;


public class ProjectNotFoundException extends CustomException {

    public ProjectNotFoundException(ExceptionMessage message, Long id) {
        super(message, id);
    }
}
