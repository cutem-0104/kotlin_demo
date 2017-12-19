package com.example.demo_todolist

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class TaskUpdateForm {

    @NotBlank
    @Size(max = 20)
    var content: String? = null
    var done: Boolean = false
}
