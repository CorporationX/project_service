package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.SubProjectDto;
import faang.school.projectservice.dto.client.UpdateSubProjectDto;
import faang.school.projectservice.service.SubProjectServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class SubProjectController {
    private SubProjectServiceImpl subProjectService;

    public SubProjectDto createSubProject(CreateSubProjectDto subProjectDto) {
        log.info("Creating subproject {}", subProjectDto.toString());
        return subProjectService.createSubProject(subProjectDto);
    }

    public void updateSubProject(UpdateSubProjectDto updateSubProjectDto) {
        log.info("Updating subproject with params: {}", updateSubProjectDto.toString());
        subProjectService.updateSubProject(updateSubProjectDto);
    }

/*    public List<Project> getSubProjectByFilter(Long projectId) {

    }*/

}
