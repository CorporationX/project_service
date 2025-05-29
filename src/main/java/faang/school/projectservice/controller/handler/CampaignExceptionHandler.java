package faang.school.projectservice.controller.handler;

import faang.school.projectservice.dto.ErrorResponseDto;
import faang.school.projectservice.exception.CampaignCreationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class CampaignExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(CampaignCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto campaignCreationException(CampaignCreationException e) {
        log.error("CampaignCreationException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Wrong campaign creation or update",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

}
