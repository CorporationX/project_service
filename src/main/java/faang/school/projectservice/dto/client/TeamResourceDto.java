package faang.school.projectservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TeamResourceDto {

    private String avatarKey;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}

