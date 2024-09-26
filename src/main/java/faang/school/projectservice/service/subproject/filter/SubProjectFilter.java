package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubProjectFilter {

    boolean isApplicable(SubProjectFilterDto subProjectFilterDto);

    Stream<Project> apply(Stream<Project> subProjects, SubProjectFilterDto subProjectFilterDto);
}