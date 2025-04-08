package faang.school.projectservice.controller;

import static faang.school.projectservice.contants.InfoMessage.*;

import faang.school.projectservice.service.AvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/avatars")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService avatarService;

    @PostMapping("/teams/{teamId}")
    public void addAvatarTeam(@PathVariable Long teamId, @RequestBody MultipartFile file) {
        log.info(INFO_START_CONTROLLER_ADD_AVATAR, teamId);
        avatarService.addAvatar(teamId, file);
    }

    @DeleteMapping("/teams/{teamId}")
    public void deleteAvatarTeam(@PathVariable Long teamId, @RequestHeader Long userId) {
        log.info(INFO_START_CONTROLLER_DELETE_AVATAR, teamId);
        avatarService.deleteAvatar(teamId, userId);
    }
}
