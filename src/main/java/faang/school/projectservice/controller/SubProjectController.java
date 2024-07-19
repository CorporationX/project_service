package faang.school.projectservice.controller;

import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.dto.validate.New;
import faang.school.projectservice.dto.validate.UpdateName;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("api/v1")
public class SubProjectController {
    private final ProjectService ProjectService;

    @PostMapping("/subprojects/")
    public SubProjectDto createSubProject(@Validated(New.class) @RequestBody SubProjectDto subProjectDto) {
        return ProjectService.createSubProject(subProjectDto);
    }

    @PutMapping("/subprojects")
    public void updateSubProject(@Validated(UpdateName.class) @RequestBody SubProjectDto subProjectDto) {
        ProjectService.updateSubProject(subProjectDto);
    }

    @GetMapping("/subprojects/{parentProjectId}")
    public List<SubProjectDto> getAllFilteredSubprojectsOfAProject(@RequestBody SubProjectFilterDto subProjectFilterDto,
                                                                   @PathVariable Long parentProjectId) {
        return ProjectService.getAllFilteredSubprojectsOfAProject(subProjectFilterDto, parentProjectId);
    }
}
