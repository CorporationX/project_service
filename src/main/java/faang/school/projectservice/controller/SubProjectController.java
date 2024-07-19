package faang.school.projectservice.controller;

import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.dto.filter.SubProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
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
@RequestMapping("projects")
public class SubProjectController {
    private final ProjectService ProjectService;

    @PostMapping("/createSubProjectDto")
    public SubProjectDto createSubProject(@RequestBody SubProjectDto subProjectDto) {
        return ProjectService.createSubProject(subProjectDto);
    }

//    @PutMapping("/createSubProjectDto")
//    public SubProjectDto updateSubProject(@RequestBody SubProjectDto subProjectDto) {
//        return ProjectService.updateSubProject(subProjectDto);
//    }

    @GetMapping("/find/all/by/{parentProjectId}")
    public List<SubProjectDto> getAllFilteredSubprojectsOfAProject(@RequestBody SubProjectFilterDto subProjectFilterDto,
                                                                   @PathVariable Long parentProjectId) {
        return ProjectService.getAllFilteredSubprojectsOfAProject(subProjectFilterDto, parentProjectId);
    }
}
