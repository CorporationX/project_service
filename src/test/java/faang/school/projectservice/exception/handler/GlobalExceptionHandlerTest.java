package faang.school.projectservice.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleJiraException() throws Exception {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorMessages(List.of("Jira error occurredн"))
                .errors(Map.of("field", "Invalid value"))
                .build();

        mockMvc.perform(get("/jira-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(errorResponse.getStatus()))
                .andExpect(jsonPath("$.errorMessages[0]").value("Jira error occurred"))
                .andExpect(jsonPath("$.errors.field").value("Invalid value"));
    }

    @Test
    void handleEntityFieldNotFoundException() throws Exception {
        String message = "Field not found";

        mockMvc.perform(get("/entity-field-not-found-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages[0]").value(message));
    }

    @Test
    void handleConstraintViolationException() throws Exception {
        String nameMessage = "must not be blank";
        String idMessage = "must be greater than or equal to 1";
        Map<String, String> violations = Map.of(
                "name", nameMessage,
                "id", idMessage
        );

        MvcResult mvcResult = mockMvc.perform(post("/method-argument-not-valid-exception")
                        .param("name", "")
                        .param("id", "-1"))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(violations, errorResponse.getErrors());
    }
}
