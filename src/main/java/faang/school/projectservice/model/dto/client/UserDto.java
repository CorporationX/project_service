package faang.school.projectservice.model.dto.client;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String token;
    private String projectUrl;
}
