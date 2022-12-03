package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.utils.TestControllerUtils.readRequest;


@ContextConfiguration(classes = {
        //mapper
 //       PersonMapperImpl.class,
        //service
 //       PersonService.class,
        //repository
   //     PersonRepository.class
})
@DisplayName("PUT Сервис изменения человека")
class PutPeopleTest extends UserControllerTest {


    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @DisplayName("1. Проверяем изменение человека на данные из json'а")
    void getIntervalsTest() throws Exception {
        MockHttpServletRequestBuilder request = put(PERSON_V1_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(readRequest("put/json/data", "person-for-change.json"));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
