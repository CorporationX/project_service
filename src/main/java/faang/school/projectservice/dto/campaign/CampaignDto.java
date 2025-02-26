package faang.school.projectservice.dto.campaign;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDto {
    @Null
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    @Positive
    private BigDecimal goal;
    @Null
    private BigDecimal amountRaised;
    private CampaignStatus status;
    @NotNull
    @Positive
    private Long projectId;
    @NotNull
    private Currency currency;
    @Null
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Null
    private Long createdBy;
    @Null
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @Null
    private Long updatedBy;

}
