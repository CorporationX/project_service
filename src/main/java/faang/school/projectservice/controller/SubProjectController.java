package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.service.SubProjectService;
import faang.school.projectservice.util.ChildrenNotFinishedException;
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

    public CreateSubProjectDto refreshSubProject(CreateSubProjectDto subProjectDto){
        try {
            return subProjectService.refreshSubProject(subProjectDto);
        } catch (ChildrenNotFinishedException e) {
            throw new RuntimeException(e);
        }
    }
}
