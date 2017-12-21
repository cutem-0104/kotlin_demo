package com.example.demo_todolist

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@RunWith(value = SpringRunner::class)
@SpringBootTest
@Sql(statements = arrayOf("DELETE FROM task"))
class JdbcTaskRepositoryTest {

    @Autowired
    private lateinit var sut: JdbcTaskRepository

    @Test
    fun なにも作成しなければfindAllは空のリストを返すこと() {
        val got = sut.findAll()
        assertThat(got).isEmpty()
    }

    @Test
    fun createで作成したタスクをfindAllですべて取得できること() {
        val tasks = listOf(sut.create("TEST2"), sut.create("TEST3"))
        val got = sut.findAll()
        assertThat(got).isEqualTo(tasks)
    }

    @Test
    fun createで作成したタスクをfindByIdで取得できること() {
        val task = sut.create("TEST1")
        val got = sut.findById(task.id)
        assertThat(got).isEqualTo(task)
    }

    @Test
    fun createで作成したタスクをupdateで更新できること() {
        val task = sut.create("update_test")
        val updateContent = "update!!!!"
        val updateDone = true
        val updateTask = task.copy(task.id, updateContent, updateDone)
        sut.update(updateTask)
        val got: Task? = sut.findById(task.id)

        assertThat(got?.id).isEqualTo(task.id)
        assertThat(got?.content).isEqualTo(updateContent)
        assertThat(got?.done).isEqualTo(updateDone)
    }

    @Test
    fun createで作成したタスクをdestroyで削除できること() {
        val task = sut.create("delete_test")
        sut.destroy(task)
        val destroyedTask: Task? = sut.findById(task.id)
        assertThat(destroyedTask).isEqualTo(null)
    }
}