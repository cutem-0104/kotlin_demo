package com.example.demo_todolist

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

// @RestControllerにすると文字列をそのまま返せる
@RestController
@RequestMapping(value = "hello")
class HelloController(private val greeter: Greeter) {

    @ExceptionHandler(RequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNotFoundException(): String = "必須パラメーターを指定してください"

    @GetMapping("")
    fun hello(@RequestParam name: String?): Hello {
        if (name == null || name.isEmpty()) {
            throw RequestParameterException()
        } else {
            return Hello(greeter.hello(name))
        }

    }
}