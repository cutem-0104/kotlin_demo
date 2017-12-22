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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringRunner::class)
@WebMvcTest(TaskController::class)
class TaskControllerTest {

    // TODO リファクタリング
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var taskRepository: TaskRepository

    @MockBean
    private lateinit var greeter: Greeter

    @MockBean
    private lateinit var commandLineRunner: CommandLineRunner

    @Test
    fun index_保存されているタスクを全件表示すること() {
        val tasks = listOf(
                Task(id = 123, content = "hoge", done = false),
                Task(id = 234, content = "bar", done = true)
        )
        // findAll呼び出し時にtasksを返すようにモックする
        Mockito.`when`(taskRepository.findAll()).thenReturn(tasks)

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
                .andExpect(view().name("tasks/index"))
                .andExpect(model().attribute("tasks", tasks))
                .andExpect(content().string(Matchers.containsString("<span>hoge</span>")))
                .andExpect(content().string(Matchers.containsString("<s>bar</s>")))
    }

    @Test
    fun create_ポストされた内容でタスクを新規作成すること() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .param("content", "piyo"))
                .andExpect(redirectedUrl("/tasks"))

        Mockito.verify(taskRepository).create("piyo")
    }

    @Test
    fun create_20文字以上のタスクが新規作成できないこと() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .param("content", "piyoooooooooooooooooooooooooooooooooooooooooooooo"))
                .andExpect(content().string(Matchers.containsString("<p>size must be between 0 and 20</p>")))
    }

    @Test
    fun create_空文字のタスクが新規作成できないこと() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .param("content", ""))
                .andExpect(content().string(Matchers.containsString("<p>must not be blank</p>")))
    }

    @Test
    fun update_タスクが更新できること() {
        val task = Task(id = 123, content = "hoge", done = false)
        val tasks = listOf(
                task,
                Task(id = 234, content = "bar", done = true)
        )
        Mockito.`when`(taskRepository.findAll()).thenReturn(tasks)
        Mockito.`when`(taskRepository.findById(tasks[0].id)).thenReturn(tasks[0])

        val updateTask = task.copy(content = "hogehoge", done = true)
        // findAll呼び出し時にtasksを返すようにモックする
        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/${task.id}")
                .param("content", updateTask.content)
                .param("done", updateTask.done.toString()))
                .andExpect(redirectedUrl("/tasks"))
        Mockito.verify(taskRepository).update(updateTask)
    }

    @Test
    fun findAllNull() {

    }

    @Test
    fun delete() {
        val tasks = listOf(
                Task(id = 123, content = "hoge", done = false),
                Task(id = 234, content = "bar", done = true)
        )
        // findAll呼び出し時にtasksを返すようにモックする
        Mockito.`when`(taskRepository.findAll()).thenReturn(tasks)
        Mockito.`when`(taskRepository.findById(tasks[0].id)).thenReturn(tasks[0])

        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/${tasks[0].id}"))
                .andExpect(redirectedUrl("/tasks"))

        Mockito.verify(taskRepository).destroy(tasks[0])
    }

    @Test
    fun detail() {
        val tasks = listOf(
                Task(id = 123, content = "hoge", done = false),
                Task(id = 234, content = "bar", done = true)
        )

        Mockito.`when`(taskRepository.findById(tasks[0].id)).thenReturn(tasks[0])

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/${tasks[0].id}"))
                .andExpect(view().name("tasks/detail"))
                .andExpect(model().attribute("task", tasks[0]))
                .andExpect(content().string(Matchers.containsString("<span type=\"text\">hoge</span>")))

        Mockito.verify(taskRepository).findById(tasks[0].id)

    }
}