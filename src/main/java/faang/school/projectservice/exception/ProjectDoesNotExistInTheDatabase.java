package faang.school.projectservice.exception;

public class ProjectDoesNotExistInTheDatabase extends RuntimeException{
    public ProjectDoesNotExistInTheDatabase(String message) {
        super(message);
    }
}
