package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.json.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
@Validated
public class VacancyController {
    private final VacancyService vacancyService;

    @GetMapping("/{id}")
    public ResponseEntity<VacancyDto> getVacancy(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.getVacancy(id));
    }

    @PostMapping("/all")
    public ResponseEntity<List<VacancyDto>> getAll(@RequestBody VacancyFilterDto filterDto) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.getAll(filterDto));
    }
    @PostMapping("/create")
    public ResponseEntity<Map<String,String>> create(@Valid @RequestBody VacancyDto vacancy) throws ValidationException {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.create(vacancy));
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String,String>> update(@PathVariable Long id, @Valid @RequestBody VacancyDto vacancy) throws ValidationException {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.update(id,vacancy));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String,String>> delete(@PathVariable Long id) throws ValidationException {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.delete(id));
    }
}
