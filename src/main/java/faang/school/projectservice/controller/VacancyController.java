package faang.school.projectservice.controller;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/vacancy")
public class VacancyController {
    private VacancyService vacancyService;

    @GetMapping("/{id}")
    public ResponseEntity<VacancyDto> getVacancy(@PathVariable Long id) {
        VacancyDto vacancyDto = vacancyService.getVacancy(id);
        return ResponseEntity.ok().body(vacancyDto);
    }

    @PostMapping("/{vacancyId}/cover")
    public ResponseEntity<String> uploadCoverImageForVacancy(
            @PathVariable Long vacancyId,
            @RequestParam("file") MultipartFile file
    ) {
        String key = vacancyService.saveCoverImage(vacancyId, file);
        return ResponseEntity.ok().body(key);
    }

    @DeleteMapping("/{vacancyId}/deleteCover")
    public ResponseEntity<Void> deleteCoverImageFromVacancy(@PathVariable Long vacancyId) {
        vacancyService.deleteCoverImageFromVacancy(vacancyId);
        return ResponseEntity.ok().build();
    }
}
