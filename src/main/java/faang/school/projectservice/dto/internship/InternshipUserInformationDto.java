package faang.school.projectservice.dto.internship;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipUserInformationDto {
    @Schema(description = "Intern id", example = "1")
    private Long id;

    @Schema(description = "User id", example = "1")
    private Long userId;

    @Schema(description = "Nickname", example = "@Vasya")
    private String nickname;
}
