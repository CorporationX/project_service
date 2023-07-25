package faang.school.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MomentDto {
    private Long id;
    private String name;
    private LocalDateTime date;
}
