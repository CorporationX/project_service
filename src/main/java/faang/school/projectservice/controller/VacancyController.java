package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacancy")
@RequiredArgsConstructor
@Validated
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public VacancyDTO create(@RequestBody @Valid VacancyDTO vacancyDTO) {
        return vacancyService.create(vacancyDTO);
    }

    @PutMapping("/{id}")
    public VacancyDTO update(@PathVariable Long id, @RequestBody @Valid VacancyDTO vacancyDTO) {
        return vacancyService.update(id, vacancyDTO);
    }

    @GetMapping("/{id}")
    public VacancyDTO getById(@PathVariable Long id) {
        return vacancyService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        vacancyService.deleteById(id);
    }

    @GetMapping
    public List<VacancyDTO> getAll(
            @RequestParam(required = false) TeamRole position,
            @RequestParam(required = false) String name) {
        return vacancyService.getAll(position, name);

    }
}
