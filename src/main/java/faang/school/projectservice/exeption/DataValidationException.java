package faang.school.projectservice.exeption;

public class DataValidationException extends RuntimeException {
  public DataValidationException(String message) {
    super(message);
  }
}
