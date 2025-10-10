package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
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

    MomentDto createMoment(@NonNull CreateMomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    MomentDto updateMoment(@NonNull UpdateMomentDto momentDto) {
        return momentService.updateMoment(momentDto);
    }

    MomentDto getById(long momentId) {
        return momentService.getById(momentId);
    }

    List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    void deleteById(long momentId){
        momentService.deleteById(momentId);
    }
}
