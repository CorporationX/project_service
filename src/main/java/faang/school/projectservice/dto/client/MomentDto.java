package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class MomentDto {

    @Positive(message = "Id is required")
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 16, message = "Username length should be min 3, max 16")
    private String name;

    @NotEmpty(message = "At least one project must be specified")
    private List<@NotNull(message = "The project ID cannot be null") Long> projectsIds;

    @Size(max = 255, message = "Description length should not exceed 255 characters")
    private String description;

    private LocalDateTime date;

    @Size(max = 255, message = "Pattern length should not exceed 255 characters")
    private String requestFilterPattern;
}
