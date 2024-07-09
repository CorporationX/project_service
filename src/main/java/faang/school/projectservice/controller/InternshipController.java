package faang.school.projectservice.controller;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.zip.DataFormatException;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private InternshipService internshipService;

    public void create(Internship internship) throws DataFormatException {
        internshipService.create(internship);
    }

}
