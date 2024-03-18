package faang.school.projectservice.validator.moment;

import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.dto.moment.MomentDto;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidatorMoment {
    private final MomentMapper momentMapper;
    public void ValidatorMomentName(MomentDto momentDto) {
        if (StringUtils.isBlank(momentDto.getName())) {
            throw new IllegalArgumentException("Moment must have a name");
        }
    }

    public void ValidatorOpenProject(MomentDto momentDto) {
        Moment moment = momentMapper.toMoment(momentDto);
        moment.getProjects().stream()
                .filter(project -> project.getStatus().equals(ProjectStatus.CANCELLED))
                .forEach(project -> {throw new IllegalArgumentException("ProjectStatus.CANCELLED");});
    }

    public void ValidatorMomentProject(MomentDto momentDto) {
        List<Long> projectIds = momentDto.getProjectIds();
        if (projectIds == null || projectIds.isEmpty()){
            throw new IllegalArgumentException("Moment has not projectIds");
        }
        for (Long projectId : projectIds) {
            if (projectId == null){
                throw new IllegalArgumentException("id does not null");
            }
        }
    }
}
//@Test
//public void testCreateMomentForClosedProject() {
//    Moment moment = new Moment();
//    moment.setTitle("Test moment");
//    moment.setProject(new Project());
//    assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(moment));