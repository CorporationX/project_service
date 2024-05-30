package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
    @Length(max = 64)
    private String name;

    @NotBlank(message = "Description should not be blank")
    @Length(max = 4098)
    private String description;

    @NotEmpty
    private List<Long> projectIds;

    @NotEmpty
    private List<Long> userIds;

    private String imageId;
}
