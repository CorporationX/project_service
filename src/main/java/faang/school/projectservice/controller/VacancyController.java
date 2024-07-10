package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Component
@RestController
@RequestMapping("/api/vacancy")
@RequiredArgsConstructor
@Validated
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping(value = "/create")
    public ResponseEntity<Map<String,String>> create(@Valid @RequestBody VacancyDto vacancy) throws ValidationException {

        return new ResponseEntity<>(vacancyService.create(vacancy), HttpStatus.CREATED);
    }
}
