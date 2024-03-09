package faang.school.projectservice.validation;

import faang.school.projectservice.exeptions.DataValidationException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
@Component
public class InternshipValidator {
    private final int MAX_DURATION_INTERNSHIP_DAYS = 91;
    public void checkInternshipDtoDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null)
            throw new DataValidationException("Invalid dates");
        if (startDate.isAfter(endDate))
            throw new IllegalArgumentException("Incorrect dates have been entered");
        Duration duration = Duration.between(startDate, endDate);
        if (duration.toDays() > MAX_DURATION_INTERNSHIP_DAYS)
            throw new DataValidationException("Internship duration cannot exceed " + MAX_DURATION_INTERNSHIP_DAYS + " days");
    }
}
