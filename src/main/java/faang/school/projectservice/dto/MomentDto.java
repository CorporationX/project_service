package faang.school.projectservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MomentDto {
    private Long id;
    private String name;
    private LocalDateTime date;
}
