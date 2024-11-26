package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.Month;
import java.util.List;

@Builder
@Data
@Validated
public class MomentFilterDto {
    private Month month;

    @NotEmpty(message = "Project IDs list cannot be empty")
    private List<Long> projectIds;

    @Size(max = 255, message = "Pattern length must not exceed 255 characters")
    private String pattern;
}
