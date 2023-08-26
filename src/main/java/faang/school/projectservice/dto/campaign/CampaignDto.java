package faang.school.projectservice.dto.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
public class CampaignDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 128, message = "Name must be less than 128 characters")
    private String title;

    @NotBlank
    @Size(max = 4096, message = "Name must be less than 4096 characters")
    private String description;

    @NotNull
    private Long projectId;

    private Currency currency;

    private LocalDateTime createdAt;
    private Long createdBy;

    private LocalDateTime updatedAt;
    private Long updatedBy;
}
