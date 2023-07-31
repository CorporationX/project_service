package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    private MockMvc mockMvc;

    @Test
    public void controllerTest() throws Exception {
        mockMvc.perform(post("/api/v1/vacancy/{creatorId}")
                .param("id", "1"))
                .andExpect(status().isOk());
                //.andExpect(content().json(VacancyDto));
    }
}
