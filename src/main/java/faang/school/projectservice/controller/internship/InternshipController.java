package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internShip.InternshipDtoValidateException;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.service.internShip.InternshipService;
import faang.school.projectservice.validator.internShip.InternshipDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipDtoValidator validator;
    private final InternshipService internshipService;

    @PostMapping("/")
    public InternshipDto create(@RequestBody InternshipDto internShipDto) {
        if (!validator.validateInternshipDto(internShipDto)) {
            throw new InternshipDtoValidateException("InternShipDto не прошёл валидацию.");
        }
        return internshipService.create(internShipDto);
    }

    @PutMapping("/")
    public InternshipDto update(@RequestBody InternshipDto internShipDto) {
        if (!validator.validateInternshipDto(internShipDto)) {
            throw new InternshipDtoValidateException("InternShipDto не прошёл валидацию.");
        }
        return internshipService.update(internShipDto);
    }

    @GetMapping("/{id}")
    public InternshipDto getInternship(@PathVariable(name = "id") Long internshipId) {
        validator.validateInternshipId(internshipId);
        return internshipService.getInternship(internshipId);

    }

    @PostMapping("/filtered")
    public List<InternshipDto> getFilteredInternship(@RequestBody InternshipFilterDto filters) {
        return internshipService.getFilteredInternship(filters);
    }

    @GetMapping("/all")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }
}
