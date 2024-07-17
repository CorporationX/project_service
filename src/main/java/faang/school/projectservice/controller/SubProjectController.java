package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.service.SubProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping("/createSubProjectDto")
    public CreateSubProjectDto createSubProject(@RequestBody CreateSubProjectDto createSubProjectDto) {
        return subProjectService.createSubProject(createSubProjectDto);
    }

    @PutMapping("/createSubProjectDto")
    public CreateSubProjectDto updateSubProject(@RequestBody CreateSubProjectDto createSubProjectDto) {
        return subProjectService.updateSubProject(createSubProjectDto);
    }

    @GetMapping("/createSubProjectDto")
    public List<CreateSubProjectDto> getAllFilteredSubprojectsOfAProject(@RequestBody CreateSubProjectDto createSubProjectDto) {
        return subProjectService.getAllFilteredSubprojectsOfAProject(createSubProjectDto);
    }
}
