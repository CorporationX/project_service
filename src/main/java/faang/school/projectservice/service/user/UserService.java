package faang.school.projectservice.service.user;

import faang.school.projectservice.dto.client.UserDto;

public interface UserService {
    UserDto getById(Long userId);
}
