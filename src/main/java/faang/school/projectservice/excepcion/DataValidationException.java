package faang.school.projectservice.excepcion;

public class DataValidationException extends RuntimeException {
  public DataValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataValidationException(String message) {
    super(message);
  }
}
