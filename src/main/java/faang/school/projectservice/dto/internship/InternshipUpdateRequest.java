package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InternshipUpdateRequest {
    @NotNull @Positive
    Long projectId;

    long id;

    @NotNull @Positive
    Long mentorId;

    List<Long> internIds;

    @NotNull(message = "Укажите время начала стажировки")
    LocalDateTime startDate;

    @NotNull(message = "Укажите время конца стажировки")
    LocalDateTime endDate;

    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 512, message = "Описание не должно превышать 128 символов")
    String description;

    @NotBlank(message = "Название не должно быть пустым")
    @Size(max = 128, message = "Название не должно превышать 128 символов")
    String name;

    TeamRole role;

    public InternshipUpdateRequest (@Valid @NotNull Long internshipId) {
        this.id = internshipId;
    }
}
