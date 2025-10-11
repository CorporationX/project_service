package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import lombok.NonNull;

import java.util.List;

public interface MomentService {
    MomentDto createMoment(@NonNull CreateMomentDto momentDto);

    MomentDto updateMoment(@NonNull UpdateMomentDto momentDto);

    MomentDto getById(long momentId);

    List<MomentDto> getAllMoments();

    List<MomentDto> getMomentsByProjectId(long projectId);



    void deleteById(long momentId);
}
