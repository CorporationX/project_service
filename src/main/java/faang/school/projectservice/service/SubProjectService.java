package faang.school.projectservice.service;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SubProjectService {

    SubProjectResponseDto createSubProject(CreateSubProjectDto subProjectDto);

    SubProjectResponseDto updateSubProject(Long id, UpdateSubProjectDto updateSubProjectDto);

    SubProjectResponseDto findById(Long id);

    List<SubProjectResponseDto> findAll();

    List<SubProjectResponseDto> findAllByFilter(SubProjectFilterDto filter);
}
