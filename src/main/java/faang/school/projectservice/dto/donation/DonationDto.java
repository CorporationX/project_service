package faang.school.projectservice.dto.donation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DonationDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long paymentNumber;

    @NotNull(message = "The amount must not be null")
    @Min(value = 1, message = "The amount must be greater or equal to 1")
    private BigDecimal amount;

    @NotNull(message = "The currency must not be null")
    @Pattern(message = "Currency code must be either 'USD' or 'EUR'", regexp = "^(USD|EUR)$")
    private String currency;

    @NotNull(message = "The campaign id must not be null")
    private Long campaignId;
}