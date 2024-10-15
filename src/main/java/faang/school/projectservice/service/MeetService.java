package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.model.dto.MeetFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface MeetService {
    @Transactional
    MeetDto create(MeetDto meetDto) throws GeneralSecurityException, IOException;

    @Transactional
    MeetDto update(long meetId, MeetDto meetDto) throws GeneralSecurityException, IOException;

    @Transactional
    void delete(Long meetId) throws GeneralSecurityException, IOException;

    List<MeetDto> getByFilter(MeetFilterDto filterDto);

    Page<MeetDto> getAll(Pageable pageable);

    MeetDto getById(Long meetId);
}
