package faang.school.projectservice.dto.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CampaignDto {
    @NotNull
    private Long id;
    @NotBlank
    @Size(max = 128, message = "Internship's name length can't be more than 128 symbols")
    private String title;
    private BigDecimal amountRaised;
}
