package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class InternshipGetAllDto {
    private Long id;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotNull(message = "Status can not be null")
    private InternshipStatus status;

    private Long createdBy;

    @Size(max = 255, message = "Pattern length should not exceed 255 characters")
    private String requestFilterPattern;
}
