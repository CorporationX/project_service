package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.feign.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public UserDto getUserDtoById(long userId) {
        try {
            userContext.setUserId(userId);
            return userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не найден " + e.getMessage());
        }
    }
}
