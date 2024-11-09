package faang.school.projectservice.dto.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDto {
    @Null(message = "Campaign id is given out by the system")
    private Long id;
    @NotBlank(message = "Campaign title can't be empty or null")
    private String title;
    private String description;
    @NotNull(message = "Can't create a campaign without a goal amount")
    private BigDecimal goal;
    @Null(message = "Can't create a campaign with non-zero initial amount")
    private BigDecimal amountRaised;
    @Null(message = "Can't create a campaign with an initial deleted state")
    private Boolean deleted;
    private CampaignStatus status;
    @NotNull(message = "Campaign must be tied to a particular currency")
    private Currency currency;
    @NotNull(message = "Campaign must be assigned to a particular project")
    private Long projectId;
    @Null(message = "Can't start a campaign on behalf of another user")
    private Long createdId;
    @Null(message = "Updating user is supposed to be established by the system")
    private Long updatedId;
    @Null(message = "Start date of campaign is given out by the system")
    private LocalDateTime createdAt;
    @Null(message = "Updating date of campaign is given out by the system")
    private LocalDateTime updatedAt;
}
