package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class StageProjectStatusFilter implements StageFilter {


    @Override
    public boolean isApplicable(StageFilterDto filters) {
        List<String> filtrationStatus = filters.getStageProjectStatus();
        if (filtrationStatus == null || filtrationStatus.isEmpty()) {
            return false;
        }

        List<String> projectStatus = ProjectStatus.getAllStatusNames();

        return filtrationStatus.stream()
                .map(String::toUpperCase)
                .allMatch(projectStatus::contains);
    }

    @Override
    public void apply(Stream<Stage> stages, StageFilterDto filters) {
        stages.filter(
                stage -> filters.getStageProjectStatus().contains(stage.getProject().getStatus().getStatusName()));
    }
}
