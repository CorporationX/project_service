package faang.school.projectservice.exception;

public class NoProjectsInTheDatabase extends RuntimeException{
    public NoProjectsInTheDatabase(String message){
        super(message);
    }
}
