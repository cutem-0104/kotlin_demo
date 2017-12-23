package com.example.demo_todolist

import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest(HelloController::class)
class HelloControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var greeter: Greeter

    @MockBean
    private lateinit var commandLineRunner: CommandLineRunner

    @Test
    fun hello() {
        Mockito.`when`(greeter.hello("Spring")).thenReturn("こんにちは,Spring!")

        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
                .param("name", "Spring"))
                .andExpect(status().isOk)
                .andExpect(content().string(Matchers.containsString("name")))
                .andExpect(content().string(Matchers.containsString("こんにちは,Spring!")))
    }

}