package faang.school.projectservice.dto.campaign;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class CampaignFilterDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    private CampaignStatus campaignStatus;

    @NotNull
    private Long createdBy;
}
