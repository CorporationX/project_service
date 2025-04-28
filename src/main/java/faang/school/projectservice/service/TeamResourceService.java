package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.TeamResourceDto;
import org.springframework.web.multipart.MultipartFile;

public interface TeamResourceService {

    TeamResourceDto uploadAvatar(Long teamId, MultipartFile file);

    void deleteAvatar(Long teamId, Long userId);
}
