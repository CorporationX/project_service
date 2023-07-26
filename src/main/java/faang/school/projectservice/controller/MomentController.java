package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/moments")
public class MomentController {
    public final MomentService momentService;

    @PostMapping("/create")
    public void createMoment(@Valid MomentDto momentDto) {
        validateDtoMoment(momentDto);
        momentService.createMoment(momentDto);
    }

    @PutMapping("/update")
    public void updateMoment(@Valid MomentDto momentDto) {
        validateDtoMoment(momentDto);
        momentService.updateMoment(momentDto);
    }

    @GetMapping("/getFiltered")
    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto) {
        return momentService.getFilteredMoments(filterMomentDto);
    }

    @GetMapping("/getAll")
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/get")
    public MomentDto getMoment(@Valid long momentId) {
        validateId(momentId);
        return momentService.getMoment(momentId);
    }

    private void validateName(String name) {
        if (name.isEmpty()) {
            throw new NullPointerException(ErrorMessages.EMPTY_NAME);
        }
    }

    private void validateDtoMoment(MomentDto momentDto) {
        if (momentDto == null || momentDto.getId() <= 0 || momentDto.getDate() == null
                || momentDto.getName().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_DTO);
        }
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_ID);
        }
    }
}
