package faang.school.projectservice.service;

import faang.school.projectservice.dto.sub_project.SubProjectCreateDto;
import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.dto.sub_project.SubProjectUpdateDto;
import faang.school.projectservice.dto.sub_project.SubProjectViewDto;

import java.util.List;

/**
 * ProjectService — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 21.07.2025
 */
public interface ProjectService {

    SubProjectViewDto create(SubProjectCreateDto createDto);

    SubProjectViewDto update(Long id, SubProjectUpdateDto updateDto);

    List<SubProjectViewDto> getByFilter(Long parentId, SubProjectFilterDto filterDto);
}