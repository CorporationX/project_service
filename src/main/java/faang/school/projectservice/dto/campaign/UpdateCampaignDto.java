package faang.school.projectservice.dto.campaign;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateCampaignDto {

    @NotNull
    private Long id;

    @NotBlank(message = " Title most not be empty! ")
    @Size(max = 128, message = "Name must be less than 128 characters")
    private String title;

    @NotBlank(message = " Description most not be empty! ")
    @Size(max = 4096, message = "Name must be less than 4096 characters")
    private String description;

    @NotNull
    private Long projectId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @NotNull
    private Long updatedBy;
}
