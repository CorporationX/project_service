package faang.school.projectservice.dto.cover;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CoverDto {
    private String name;
    private String type;
    private long size;
}
