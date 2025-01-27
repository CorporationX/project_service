package faang.school.projectservice.dto.internship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipUserInformationDto {
    private Long id;
    private Long userId;
    private String nickname;
}
