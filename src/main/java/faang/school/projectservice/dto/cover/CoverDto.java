package faang.school.projectservice.dto.cover;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Data
@Getter
@Setter
public class CoverDto {
    private String name;
    private String type;
    private long size;

}
