package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.SearchMomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import lombok.NonNull;
import java.util.List;

public interface MomentService {
    MomentDto createMoment(@NonNull CreateMomentDto momentDto)  throws Exception;

    MomentDto updateMoment(long momentID, @NonNull UpdateMomentDto momentDto) throws Exception;

    MomentDto getById(long momentId);

    List<MomentDto> getAllMoments();

    List<MomentDto> getMomentsByProjectId(long projectId);

    List<MomentDto> getMomentsByMonth(SearchMomentDto searchMomentDto);

    void deleteById(long momentId);
}
