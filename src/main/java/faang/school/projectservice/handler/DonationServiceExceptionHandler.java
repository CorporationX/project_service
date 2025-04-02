package faang.school.projectservice.handler;

import faang.school.projectservice.controller.DonationController;
import faang.school.projectservice.exception.ErrorResponse;
import faang.school.projectservice.exception.campaign.CampaignCanceledException;
import faang.school.projectservice.exception.campaign.CampaignCompletedException;
import faang.school.projectservice.exception.campaign.CampaignNotFoundException;
import faang.school.projectservice.exception.campaign.UnknownCampaignStatusException;
import faang.school.projectservice.exception.payment.PaymentFailedException;
import faang.school.projectservice.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice(assignableTypes = DonationController.class)
public class DonationServiceExceptionHandler {

    @ExceptionHandler(CampaignNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCampaignNotFoundException(CampaignNotFoundException exception, WebRequest request) {
        log.error("CampaignNotFoundException caught: {}", exception.getMessage());

        return new ErrorResponse(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(CampaignCompletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCampaignCompletedException(CampaignCompletedException exception, WebRequest request) {
        log.error("CampaignCompletedException caught: {}", exception.getMessage());

        return new ErrorResponse(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler(CampaignCanceledException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCampaignCanceledException(CampaignCanceledException exception, WebRequest request) {
        log.error("CampaignCanceledException caught: {}", exception.getMessage());

        return new ErrorResponse(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler(UnknownCampaignStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownCampaignStatusException(
            UnknownCampaignStatusException exception,
            WebRequest request
    ) {
        log.error("UnknownCampaignStatusException caught: {}", exception.getMessage());

        return new ErrorResponse(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler(PaymentFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePaymentFailedException(PaymentFailedException exception, WebRequest request) {
        log.error("PaymentFailedException caught: {}", exception.getMessage());

        return new ErrorResponse(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException exception, WebRequest request) {
        log.error("UserNotFoundException caught: {}", exception.getMessage());

        return new ErrorResponse(HttpStatus.NOT_FOUND, exception, request);
    }
}
