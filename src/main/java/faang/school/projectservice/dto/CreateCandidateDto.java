package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCandidateDto {
    @NotNull
    private Long userId;

    @NotNull
    private String username;

    @NotNull
    private String resumeDocKey;

    private String coverLetter;
}
