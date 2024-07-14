package faang.school.projectservice.dto.client;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageDeleteTaskStrategyDto {
    @NotNull
    private TasksHandlingStrategy strategy;
}
