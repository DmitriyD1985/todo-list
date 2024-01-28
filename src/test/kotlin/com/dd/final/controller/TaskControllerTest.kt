package com.dd.final.controller

import com.dd.final.dto.AddTaskRequest
import com.dd.final.util.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest
class TaskControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    private val addTaskRequest = AddTaskRequest(1, "mnogo del")

    @Test
    @WithMockUser(username = "user", password = "password", authorities = ["ROLE_USER"])
    fun add() {
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/task/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", authorities = ["ROLE_USER"])
    fun get() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/task/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", authorities = ["ROLE_ADMIN"])
    fun update() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/task/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", authorities = ["ROLE_USER"])
    fun updateNegative() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/task/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", authorities = ["ROLE_ADMIN"])
    fun delete() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/task/delete/{id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", authorities = ["ROLE_USER"])
    fun deleteNegative() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/task/delete/{id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn().response
    }

}