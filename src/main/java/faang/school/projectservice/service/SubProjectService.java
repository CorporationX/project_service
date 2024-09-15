package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import org.springframework.stereotype.Service;


public interface SubProjectService {

    public CreateSubProjectDto createSubProject(CreateSubProjectDto subProjectDto);

    public CreateSubProjectDto refreshSubProject(CreateSubProjectDto subProjectDto) throws ChildrenNotFinishedException;

}
