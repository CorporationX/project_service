package faang.school.projectservice.filterMoment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterMoment {
    private final momentMapper momentMapper;
    private final Project project;

    public momentDto filterMomentName(momentDto) {
        if (StringUtils.isBlank(momentDto.getName())) {
            throw new IllegalArgumentException("Moment must have a name");
        }
        return momentDto;
    }

    public momentDto filterOpenProject(momentDto) {
        if (project.getStatus() != ProjectStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Moment cannot be created for a closed project");
        }
        return momentDto;
    }

}
//@Test
//public void testCreateMomentForClosedProject() {
//    Moment moment = new Moment();
//    moment.setTitle("Test moment");
//    moment.setProject(new Project());
//    assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(moment));