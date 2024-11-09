package faang.school.projectservice.dto.campaign;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CampaignFilterDto {
    @Size(max = 255, message = "title cannot exceed 255 characters")
    private String title;
    @Positive(message = "must be greater than zero")
    private Long createdById;
    @DecimalMin(value = "0.01", message = "must be greater than 0")
    private BigDecimal minGoal;
    @DecimalMax(value = "1000000000.00", message = "must be less than or equal to 1,000,000.00")
    private BigDecimal maxGoal;
    private CampaignStatus status;
    @PastOrPresent(message = "can't be in future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAfter;
}
