package faang.school.projectservice.filter.impl;

import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.model.dto.StageFilterDto;
import faang.school.projectservice.model.entity.Stage;

import java.util.stream.Stream;

public class ProjectStatusFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.getProjectStatus() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters) {
        return stages.filter(stage -> stage.getProject().getStatus().equals(filters.getProjectStatus()));
    }
}
