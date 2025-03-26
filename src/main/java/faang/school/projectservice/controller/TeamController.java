package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.TeamService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {
    private final TeamService service;
    private final UserContext userContext;


    @PostMapping("/upload/{id}")
    public void upload(@NotNull @RequestBody MultipartFile file, @NotNull @PathVariable Long id) {
        service.upload(file, id);
    }

    @DeleteMapping("delete/avatar/{id}")
    public void delete(@NotNull @PathVariable Long id) {
        service.deleteAvatar(id, userContext.getUserId());
    }
}
