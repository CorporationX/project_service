package faang.school.projectservice.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TeamAvatarService {

    String addTeamAvatar(@NotNull Long userId, MultipartFile file);

    String removeTeamAvatar(@NotNull Long userId);

}
