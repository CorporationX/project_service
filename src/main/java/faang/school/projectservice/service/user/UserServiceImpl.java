package faang.school.projectservice.service.user;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.exeption.ServiceUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserServiceClient userServiceClient;

    @Override
    public UserDto getById(Long userId) {
        try {
            return userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        } catch (FeignException e) {
            log.error("Error while checking user existence: {}", e.getMessage());
            throw new ServiceUnavailableException("User service unavailable");
        }
    }
}
