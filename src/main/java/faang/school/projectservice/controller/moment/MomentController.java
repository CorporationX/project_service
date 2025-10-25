package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/v1")
@Validated
public interface MomentController {
    int DEFAULT_PAGE_SIZE = 20;

    @PostMapping("/moments")
    @ResponseStatus(HttpStatus.CREATED)
    MomentDto create(@RequestBody @Valid CreateMomentDto createMomentDto);

    @PutMapping("/moments/{momentId}")
    MomentDto update(
            @PathVariable
            @NotNull(message = "Moment id must not be null")
            @Positive(message = "Moment id must be positive")
            Long momentId,
            @RequestBody
            @Valid
            UpdateMomentDto updateMomentDto);

    @GetMapping("/moments")
    Page<MomentDto> getAll(@PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable);

    @GetMapping("/moments/{momentId}")
    MomentDto getMomentById(
            @PathVariable
            @NotNull(message = "Moment id must not be null")
            @Positive(message = "Moment id must be positive")
            Long momentId
    );

    @GetMapping("/projects/{projectId}/moments")
    Page<MomentDto> getMomentsByProject(
            @PathVariable
            @NotNull(message = "Project id must not be null")
            @Positive(message = "Project id must be positive")
            Long projectId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) List<Long> partnerProjectIds,
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable
    );
}
