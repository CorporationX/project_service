package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.service.SubProjectService;
import org.springframework.stereotype.Controller;

@Controller
public class SubProjectController {
    SubProjectService subProjectService;
    public CreateSubProjectDto createSubProject(CreateSubProjectDto subProjectDto){
        if (subProjectDto == null|| subProjectDto.getId() == null){
            throw new NullPointerException();
        }
        return subProjectService.createSubProject(subProjectDto);
    }
}
