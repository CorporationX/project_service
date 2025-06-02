package faang.school.projectservice.controller;

import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping("/{vacancyId}/cover")
    public ResponseEntity<String> uploadCover(@PathVariable Long vacancyId, @RequestParam("file") MultipartFile file) throws Exception {
        vacancyService.uploadCover(vacancyId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body("Cover image uploaded");
    }

    @DeleteMapping("/{vacancyId}/cover")
    public ResponseEntity<Void> deleteCover(@PathVariable Long vacancyId) throws Exception {
        vacancyService.deleteCover(vacancyId);
        return ResponseEntity.noContent().build();
    }
}
