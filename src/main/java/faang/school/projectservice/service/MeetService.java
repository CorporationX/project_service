package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.model.dto.MeetFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface MeetService {
    MeetDto create(MeetDto meetDto) throws GeneralSecurityException, IOException;

    MeetDto update(long meetId, MeetDto meetDto) throws GeneralSecurityException, IOException;

    void delete(Long meetId) throws GeneralSecurityException, IOException;

    List<MeetDto> getByFilter(MeetFilterDto filterDto);

    Page<MeetDto> getAll(Pageable pageable);

    MeetDto getById(Long meetId);
}
