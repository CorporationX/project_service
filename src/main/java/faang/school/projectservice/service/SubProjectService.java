package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.SubProjectDto;
import faang.school.projectservice.dto.client.UpdateSubProjectDto;


public interface SubProjectService {

    SubProjectDto createSubProject(CreateSubProjectDto subProjectDto);

    void updateSubProject(UpdateSubProjectDto updateSubProjectDto);

}
