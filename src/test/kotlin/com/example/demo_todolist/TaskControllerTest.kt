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

    // lateinitで初期化を遅延させる プロパティ宣言時の初期化を回避する
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var taskRepository: TaskRepository

    // テストで使ってないけど、TaskControllerの初期化時に渡しているので必要
    @MockBean
    private lateinit var greeter: Greeter

    @MockBean
    private lateinit var commandLineRunner: CommandLineRunner

    // タスクのモック
    private val tasks = listOf(
            Task(id = 123, content = "hoge", done = false),
            Task(id = 234, content = "bar", done = true)
    )

    @Test
    fun index_保存されているタスクを全件表示すること() {
        // findAll呼び出し時にtasksを返すようにモックする
        Mockito.`when`(taskRepository.findAll()).thenReturn(tasks)

        // tasks遷移時の挙動をテストする
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
                // 参照されるviewはtask/indexであること
                .andExpect(view().name("tasks/index"))
                // Modelオブジェクトに対してtasksがtasksという名前で追加されること
                .andExpect(model().attribute("tasks", tasks))
                // 表示されるhtmlに以下の内容が含まれること
                .andExpect(content().string(Matchers.containsString("<span>hoge</span>")))
                .andExpect(content().string(Matchers.containsString("<s>bar</s>")))
    }

    @Test
    fun create_ポストされた内容でタスクを新規作成すること() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                // ポスト時にcontent=piyoのパラメーターを与える
                .param("content", "piyo"))
                // /tasksにリダイレクトされること
                .andExpect(redirectedUrl("/tasks"))

        // taskRepositoryのcreateメソッドが一回実行されること
        Mockito.verify(taskRepository).create("piyo")
    }

    @Test
    fun create_20文字以上のタスクが新規作成できないこと() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .param("content", "piyoooooooooooooooooooooooooooooooooooooooooooooo"))
                // リクエスト時にhtmlに下記内容が表示されること
                .andExpect(content().string(Matchers.containsString("<p>size must be between 0 and 20</p>")))
    }

    @Test
    fun create_空文字のタスクが新規作成できないこと() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .param("content", ""))
                // リクエスト時にhtmlに下記内容が表示されること
                .andExpect(content().string(Matchers.containsString("<p>must not be blank</p>")))
    }

    @Test
    fun update_タスクが更新できること() {
        // 更新用Taskオブジェクト
        val updateTask = tasks[0].copy(content = "hogehoge", done = true)

        // findAll呼び出し時にtasksを返すようにモックする
        Mockito.`when`(taskRepository.findAll()).thenReturn(tasks)
        // findById呼び出し時にtasks[0]を返すようにモックする
        Mockito.`when`(taskRepository.findById(tasks[0].id)).thenReturn(tasks[0])

        // tasks[0]のidで更新処理を実行
        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/${tasks[0].id}")
                // patch時にupdateTaskのプロパティを渡す
                .param("content", updateTask.content)
                .param("done", updateTask.done.toString()))
                // /tasksにリダイレクトされること
                .andExpect(redirectedUrl("/tasks"))

        // taskRepositoryのupdateメソッドが実行されること
        Mockito.verify(taskRepository).update(updateTask)
    }

    @Test
    fun findAll_タスクが存在しない場合は無いもないことが表示されること() {
        // 空のタスクリスト
        val tasks: List<Task> = listOf()

        // findAll呼び出し時に空のリストを返すようにモックする
        Mockito.`when`(taskRepository.findAll()).thenReturn(tasks)

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
                .andExpect(view().name("tasks/index"))
                .andExpect(model().attribute("tasks", tasks))
                // 表示されるhtmlに下記の内容が含まれていること
                .andExpect(content().string(Matchers.containsString("<p>タスクがありません</p>")))
    }

    @Test
    fun delete_作成したタスクが削除されること() {
        // findAll呼び出し時にtasksを返すようにモックする
        Mockito.`when`(taskRepository.findAll()).thenReturn(tasks)
        Mockito.`when`(taskRepository.findById(tasks[0].id)).thenReturn(tasks[0])

        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/${tasks[0].id}"))
                .andExpect(redirectedUrl("/tasks"))

        // taskRepositoryのdestoryメソッドが実行されること
        Mockito.verify(taskRepository).destroy(tasks[0])
    }

    @Test
    fun detail_作成したタスクの詳細画面が表示されること() {
        Mockito.`when`(taskRepository.findById(tasks[0].id)).thenReturn(tasks[0])

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/${tasks[0].id}"))
                .andExpect(view().name("tasks/detail"))
                .andExpect(model().attribute("task", tasks[0]))
                // 表示されるhtmlに下記の内容が含まれていること
                .andExpect(content().string(Matchers.containsString("<span type=\"text\">hoge</span>")))

        // taskRepositoryのfindByIdメソッドが実行されること
        Mockito.verify(taskRepository).findById(tasks[0].id)

    }
}