package faang.school.projectservice.exception;

public class VacancyNotFoundException extends RuntimeException {
  public VacancyNotFoundException(String message) {
    super(message);
  }
}
