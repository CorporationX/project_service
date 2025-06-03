package faang.school.projectservice.exception;

public class BusinessValidationException extends RuntimeException {
  public BusinessValidationException(String message) {
    super(message);
  }
}
