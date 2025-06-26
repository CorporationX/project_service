package faang.school.projectservice.controller;

import faang.school.projectservice.service.minio.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "Аватарка команды", description = "Операции с аватаркой")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "Загрузить аватарку команды",
            description = "В случае если изображения превысит 5MB, оно будет сжато до 512 по каждой стороне")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка успешно загружена",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "File uploaded successfully: avatar.png"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("{teamId}/uploadAvatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable long teamId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }
        String fileName = file.getOriginalFilename();
        teamService.uploadFile(teamId, file, fileName);

        return ResponseEntity.ok("File uploaded successfully: " + fileName);
    }

    //метод для проверки других методов, его можно не проверять.
    @GetMapping("/avatar/{fileName}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String fileName) {
        byte[] imageBytes = teamService.getImage(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageBytes.length);
        if (imageBytes.length != 0) {
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Удалить аватарку команды",
            description = "Удаление аватарки доступно только менеджеру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка успешно удалена",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Avatar deleted"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/deleteAvatar/{managerId}/{fileName}")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long managerId, @PathVariable String fileName) {
        boolean deleted = teamService.deleteImage(managerId, fileName);
        if (deleted) {
            return new ResponseEntity<>("Avatar deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Only manager can delete avatar", HttpStatus.BAD_REQUEST);
        }
    }
}