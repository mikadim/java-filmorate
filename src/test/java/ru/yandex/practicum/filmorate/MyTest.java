package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.utils.TestControllerUtils.readRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class MyTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("1. Проверяем сохранение списка людей")
    void getIntervalsTest() throws Exception {
        mockMvc.perform(
                post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(readRequest("json", "user.json")));

//        mockMvc.perform(request)
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0]").value(1))
//                .andExpect(jsonPath("$[1]").value(2))
//                .andExpect(jsonPath("$[2]").value(3));}


}}
