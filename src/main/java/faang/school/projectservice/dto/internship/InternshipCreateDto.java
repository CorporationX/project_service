package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipCreateDto {
    @NotBlank(message = "Название стажировки отсутсвует")
    private String name;
    @Min(value = 1, message = "id указан неверно")
    private long projectId;
    @Min(value = 1, message = "id указан неверно")
    private long mentorId;
    @NotBlank(message = "Описание не может быть путсым")
    private String description;
    @NotEmpty(message = "список стажеров не может быть пустым")
    private List<Long> internsId;
    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата не актуальна")
    private LocalDateTime startDate;
    @NotNull(message = "Дата окончания не может быть пустой")
    @FutureOrPresent(message = "Дата не актуальна")
    private LocalDateTime endDate;
    @NotNull(message = "Статус не может быть пустой")
    private InternshipStatus status;
    @NotNull (message = "Роль не может быть пустой")
    private TeamRole role;
}
