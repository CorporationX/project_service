package faang.school.projectservice.dto.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiativeDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Long curatorId;
    @NotNull
    private InitiativeStatus status;
    private List<Long> stagesIds; //TODO:после merge заменить Long на StageDto и доработать логику
    @NotNull
    private Long projectId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
}
