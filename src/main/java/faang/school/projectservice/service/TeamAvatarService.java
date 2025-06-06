package faang.school.projectservice.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TeamAvatarService {

    ResponseEntity<String> addTeamAvatar(Long userId, MultipartFile file);

    ResponseEntity<String> removeTeamAvatar(@NotNull Long userId);

}
