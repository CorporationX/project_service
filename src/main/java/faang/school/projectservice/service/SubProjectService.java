package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubProjectService {

    public CreateSubProjectDto createSubProject(@RequestBody CreateSubProjectDto createSubProjectDto) {

    }

    public CreateSubProjectDto updateSubProject(@RequestBody CreateSubProjectDto createSubProjectDto) {

    }

    public List<CreateSubProjectDto> getAllFilteredSubprojectsOfAProject(@RequestBody CreateSubProjectDto createSubProjectDto) {


    }
}
