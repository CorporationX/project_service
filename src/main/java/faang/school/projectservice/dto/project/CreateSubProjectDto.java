package faang.school.projectservice.dto.project;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    @NotNull(message = "Поле parentId не должно быть пустым.")
    @Positive(message = "Поле parentId должно быть положительным числом")
    private Long parentId;
    @NotBlank(message = "Поле name не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 255, message = "Описание не должно превышать 255 символов.")
    private String description;
}