package faang.school.projectservice.dto.google.calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CalendarEventDto {

    private long id;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must be less than or equal to 255 characters")
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 255, message = "Description must be less than or equal to 255 characters")
    private String description;

    @NotNull(message = "Start date must not be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date must not be null")
    private LocalDateTime endDate;

    //TODO еще будут поля скорее всего, разберусь с этим при реализации интеграции
}