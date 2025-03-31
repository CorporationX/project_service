package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/vacancies")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost", "http://127.0.0.1:5500/"})
@Tag(name = "Vacancy API", description = "Super API to interact with vacancy table")
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping()
    @Operation(summary = "Open vacancy")
    public void openVacancy(@RequestBody OpenVacancyRequestDto requestDto) {
        vacancyService.openVacancy(requestDto);
    }

    @PutMapping()
    @Operation(summary = "Update vacancy")
    public VacancyResponseDto updateVacancy(@RequestBody UpdateVacancyRequestDto requestDto) {
        return vacancyService.updateVacancy(requestDto);
    }

    @GetMapping("/search")
    public List<VacancyResponseDto> getFilteredVacancies(@ModelAttribute FilterVacancyRequestDto filterDto) {
        return vacancyService.getFilteredVacancies(filterDto);
    }

    @GetMapping("/{vacancyId}")
    @Operation(summary = "Get vacancy by id", description = "Returns a vacancy DTO")
    public Optional<VacancyResponseDto> getVacancyById(@PathVariable long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

    @PutMapping("/cover/{vacancyId}")
    public String addCoverToVacancy(@PathVariable Long vacancyId,
                                    @RequestParam("cover") MultipartFile cover) {
        return vacancyService.addOrChangeCoverToVacancy(vacancyId, cover);
    }

    @GetMapping(value = "/cover/{vacancyId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getCoverFromVacancy(@PathVariable Long vacancyId) {
        try (InputStream inputStream = vacancyService.getVacancyCover(vacancyId)) {
            if (inputStream == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = inputStream.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(imageBytes.length);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error loading image" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }

    @DeleteMapping("/cover/{vacancyId}")
    public ResponseEntity<String> deleteCoverFromVacancy(@PathVariable long vacancyId) {
        vacancyService.deleteCoverFromVacancy(vacancyId);
        return ResponseEntity.ok("Cover deleted successfully");
    }

}
