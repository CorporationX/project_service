package faang.school.projectservice.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {
    private Long id;
    @NotBlank(message = "Project must have a name")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;
    private String key;
    private BigInteger size;
}
