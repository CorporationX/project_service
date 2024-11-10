package faang.school.projectservice.controller.mentorship;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/internships")
@RequiredArgsConstructor
@Slf4j
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto createInternship(@RequestBody InternshipDto internshipDto) {
        if (internshipDto.getInternIds().isEmpty()) {
            log.error("Список стажеров пустой.");
            throw new IllegalArgumentException("Список стажеров не должен быть пустым");
        }
        log.info("Открыта новая стажировка.");
        return internshipService.createInternship(internshipDto);
    }

    @PutMapping("/{id}")
    public InternshipDto updateInternship(@PathVariable long id, @RequestBody InternshipDto internshipDto) {
        log.info("Вношу изменения в стажировку #{}.", id);
        return internshipService.updateInternship(id, internshipDto);
    }

    @GetMapping
    public List<InternshipDto> getAllInternships(@RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String role) {
        log.info("Запрашиваю список всех стажировок с фильтрами, по {}, и {}", status, role);
        return internshipService.getAllInternships(status, role);
    }

    @GetMapping("/{id}")
    public InternshipDto getInternshipById(@PathVariable long id) {
        log.info("Запрашиваю все стажировки по {} id.", id);
        return internshipService.getInternshipById(id);
    }
}
