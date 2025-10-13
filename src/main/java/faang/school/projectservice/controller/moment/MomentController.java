package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.SearchMomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    public MomentDto createMoment(@NonNull CreateMomentDto momentDto) throws Exception {
        return momentService.createMoment(momentDto);
    }

    public MomentDto updateMoment(long momentId, @NonNull UpdateMomentDto momentDto) throws Exception {
        return momentService.updateMoment(momentId, momentDto);
    }

    public MomentDto getById(long momentId) {
        return momentService.getById(momentId);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public List<MomentDto> getMomentsByProjectId(long projectId) {
        return momentService.getMomentsByProjectId(projectId);
    }

    public List<MomentDto> getMomentsByMonth(SearchMomentDto searchMomentDto) {
        return momentService.getMomentsByMonth(searchMomentDto);
    }

    public void deleteById(long momentId){
        momentService.deleteById(momentId);
    }
}
