package faang.school.projectservice.controller;

import faang.school.projectservice.service.cover.CoverServiceImpl;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/cover")
@RequiredArgsConstructor
public class CoverController {
    private final CoverServiceImpl coverService;

    @PostMapping("/upload")
    public String uploadCoverToVacancy(@RequestParam("file") @NotNull MultipartFile multipartFile,
                                      @RequestParam("id") @NotNull Long vacancyId) {
        log.info("Received request to upload cover");
        String coverImageKey = coverService.uploadCover(multipartFile, vacancyId);
        log.info("File successfully uploaded");
        return coverImageKey;
    }
}
