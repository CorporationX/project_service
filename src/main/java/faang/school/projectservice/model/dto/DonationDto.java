package faang.school.projectservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DonationDto {
    @NotNull
    private Long id;
}
