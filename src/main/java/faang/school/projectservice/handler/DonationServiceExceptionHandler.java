package faang.school.projectservice.handler;

import faang.school.projectservice.controller.DonationController;
import faang.school.projectservice.exception.ErrorResponse;
import faang.school.projectservice.exception.campaign.CampaignCanceledException;
import faang.school.projectservice.exception.campaign.CampaignCompletedException;
import faang.school.projectservice.exception.campaign.CampaignNotFoundException;
import faang.school.projectservice.exception.campaign.UnknownCampaignStatusException;
import faang.school.projectservice.exception.donation.DonationNotFoundException;
import faang.school.projectservice.exception.donation.ExceedDonationAmountException;
import faang.school.projectservice.exception.payment.PaymentFailedException;
import faang.school.projectservice.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice(assignableTypes = DonationController.class)
public class DonationServiceExceptionHandler {

    private static final String CAUGHT_EXCEPTION = "{} caught: {}";

    @ExceptionHandler(CampaignNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCampaignNotFoundException(
            CampaignNotFoundException exception,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        registerException(exception);
        ErrorResponse errorResponse = new ErrorResponse(status, exception, request);

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(CampaignCompletedException.class)
    public ResponseEntity<ErrorResponse> handleCampaignCompletedException(
            CampaignCompletedException exception,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        registerException(exception);
        ErrorResponse errorResponse = new ErrorResponse(status, exception, request);

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(CampaignCanceledException.class)
    public ResponseEntity<ErrorResponse> handleCampaignCanceledException(
            CampaignCanceledException exception,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        registerException(exception);
        ErrorResponse errorResponse = new ErrorResponse(status, exception, request);

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(UnknownCampaignStatusException.class)
    public ResponseEntity<ErrorResponse> handleUnknownCampaignStatusException(
            UnknownCampaignStatusException exception,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        registerException(exception);
        ErrorResponse response = new ErrorResponse(status, exception, request);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ExceedDonationAmountException.class)
    public ResponseEntity<ErrorResponse> handleExceedDonationAmountException(
            ExceedDonationAmountException exception,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        registerException(exception);
        ErrorResponse response = new ErrorResponse(status, exception, request);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailedException(
            PaymentFailedException exception,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        registerException(exception);
        ErrorResponse response = new ErrorResponse(status, exception, request);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException exception,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        registerException(exception);
        ErrorResponse response = new ErrorResponse(status, exception, request);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(DonationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDonationNotFoundException(
            DonationNotFoundException exception,
            WebRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        registerException(exception);
        ErrorResponse response = new ErrorResponse(status, exception, request);

        return new ResponseEntity<>(response, status);
    }

    private void registerException(Exception exception) {
        log.error(CAUGHT_EXCEPTION, exception.getClass(), exception.getMessage(), exception);
    }
}
