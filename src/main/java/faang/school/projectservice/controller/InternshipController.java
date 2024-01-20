package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.util.validator.InternshipControllerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internships")
@RequiredArgsConstructor
@Slf4j
public class InternshipController {

    private final InternshipService internshipService;
    private final UserContext userContext;
    private final InternshipControllerValidator validator;

    @PostMapping("/")
    InternshipDto create(@Valid @RequestBody InternshipDto dto) {
        log.info("Dto: {}", dto);

        validator.validateToCreate(dto);

        InternshipDto resultDto = internshipService.create(dto, userContext.getUserId());

        return resultDto;
    }
}
