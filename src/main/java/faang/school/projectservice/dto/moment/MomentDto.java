package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomentDto {

    @Positive(message = "Id should be positive")
    @NotNull(message = "Id should not be null")
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @Size(max = 64)
    private String name;

    @NotBlank(message = "Description should not be blank")
    @Size(max = 4098)
    private String description;

    private List<Long> projectIds;

    private List<Long> userIds;

    private String imageId;
}
