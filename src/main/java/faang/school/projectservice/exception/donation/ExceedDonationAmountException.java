package faang.school.projectservice.exception.donation;

public class ExceedDonationAmountException extends RuntimeException {

  public ExceedDonationAmountException(String message) {
    super(message);
  }
}
