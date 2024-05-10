package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.validation.MomentValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moments")
public class MomentController {

    private final MomentService momentService;
    private final MomentValidation momentValidation;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody MomentDto moment) {
        momentValidation.nameIsFilled(moment.getName());
        momentService.create(moment);
    }
}
