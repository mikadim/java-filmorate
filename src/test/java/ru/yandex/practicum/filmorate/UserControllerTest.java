package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;

@ContextConfiguration(classes = {
        //controller
        UserController.class,
        //converters
        DefaultFormattingConversionService.class
})
public abstract class UserControllerTest{


    protected MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FormattingConversionService conversionService;

    @BeforeEach
    public void setUp() {
        applicationContext.getBeansOfType(Formatter.class).values().forEach(conversionService::addFormatter);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setConversionService(conversionService)
                .build();
    }

    protected void addConversionService(Class<? extends Converter<?, ?>> service) {
        Converter<?, ?> bean = applicationContext.getBean(service);
        conversionService.addConverter(bean);
    }
}