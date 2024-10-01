package faang.school.projectservice.repository;

import faang.school.projectservice.dto.moment.MomentDto;

import java.util.List;


public interface MomentService {

    public MomentDto createMoment(MomentDto momentDto);

    public MomentDto updateMoment(Long id, MomentDto momentDto);

    public MomentDto getMomentById(Long id);

    public List<MomentDto> getAllMoment();
}
