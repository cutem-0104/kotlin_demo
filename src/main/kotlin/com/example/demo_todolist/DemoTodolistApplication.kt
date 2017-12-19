package com.example.demo_todolist

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoTodolistApplication

fun main(args: Array<String>) {
    runApplication<DemoTodolistApplication>(*args)
}
