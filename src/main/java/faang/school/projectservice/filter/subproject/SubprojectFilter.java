package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubprojectFilter {
    boolean isApplicable(SubprojectFilterDto filters);

    Stream<Project> apply(Stream<Project> projects, SubprojectFilterDto filters);
}
