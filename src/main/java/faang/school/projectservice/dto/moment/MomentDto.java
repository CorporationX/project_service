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

    @NotNull(message = "Id should not be null")
    @Positive(message = "Id should be positive")
    private Long id;

    @NotNull(message = "Name should not be null")
    @NotBlank(message = "Name should not be blank")
    @Length(max = 64, message = "Name length should be less than 64")
    private String name;

    @NotNull(message = "Description should not be null")
    @NotBlank(message = "Description should not be blank")
    @Length(max = 4096, message = "Description length should be less than 4096")
    private String description;

    @NotNull(message = "ProjectIds should not be null")
    @NotEmpty(message = "ProjectIds should not be empty")
    private List<Long> projectIds;

    @NotNull(message = "UserIds should not be null")
    @NotEmpty(message = "UserIds should not be empty")
    private List<Long> userIds;

    private String imageId;
}
