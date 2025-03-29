package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/teams/{teamId}")
    public ResponseEntity<Map<String, Object>> uploadAvatarForTeam(
            @PathVariable Long teamId,
            @RequestParam MultipartFile file,
            @RequestHeader("X-User-Id") Long userId) {
        ResourceDto resourceDto = resourceService.uploadAvatarForTeam(teamId, file);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Avatar for team successful upload");
        response.put("data", resourceDto);
        return ResponseEntity.ok(response);
    }
}
