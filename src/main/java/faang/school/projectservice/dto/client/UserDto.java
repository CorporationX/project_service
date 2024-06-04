package faang.school.projectservice.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User entity")
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
