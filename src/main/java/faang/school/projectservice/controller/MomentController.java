package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/moment")
public class MomentController {

    private final MomentService momentService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public MomentDto create(@RequestBody @Valid MomentDto momentDto) {
        return momentService.create(momentDto);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public MomentDto update(@RequestBody @Valid MomentDto momentDto) {
        return momentService.update(momentDto);
    }

    @GetMapping("/filtered")
    public List<MomentDto> getAllMomentsFiltered(@RequestBody MomentFilterDto filters) {
        return momentService.getFilteredMomentsOfProject(filters);
    }

    @GetMapping("/")
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{momentId}")
    public MomentDto getMoment(@PathVariable long momentId) {
        return momentService.getMoment(momentId);
    }

    @DeleteMapping("/{momentId}")
    public void deleteMoment(@PathVariable long momentId) {
        momentService.delete(momentId);
    }
}
