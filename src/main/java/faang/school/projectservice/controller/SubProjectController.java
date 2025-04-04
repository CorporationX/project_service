package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/subprojects")
@RequiredArgsConstructor
public class SubProjectController {

    private final SubProjectService subProjectService;

    @PostMapping
    public SubProjectResponseDto createSubProject(@RequestBody CreateSubProjectDto createSubProjectDto) {
        log.info("#SubProjectController: Creating subproject with data: {}", createSubProjectDto);
        return subProjectService.createSubProject(createSubProjectDto);
    }

    @PutMapping("/{id}")
    public SubProjectResponseDto updateSubProject(@PathVariable Long id,
                                                  @RequestBody UpdateSubProjectDto updateSubProjectDto) {
        log.info("#SubProjectController: Updating subproject with id: {} and data: {}", id, updateSubProjectDto);
        return subProjectService.updateSubProject(id, updateSubProjectDto);
    }

    @GetMapping("/search")
    public List<SubProjectResponseDto> findAllByFilter(SubProjectFilterDto filter) {
        log.info("#SubProjectController: request to find all subprojects matching the filter:[{}] has been received",
                filter);
        return subProjectService.findAllByFilter(filter);
    }

    @GetMapping
    public List<SubProjectResponseDto> findAll() {
        log.info("#SubProjectController: request to find all subprojects has been received");
        return subProjectService.findAll();
    }

    @GetMapping("/{id}")
    public SubProjectResponseDto findById(@PathVariable Long id) {
        log.info("#SubProjectContoller: request to find a subproject by its id:{} has been received", id);
        return subProjectService.findById(id);
    }
}