package com.example.obd2app.obd2

import com.example.obd2app.obd2.command.RawResponse

abstract class ObdCommand {
    abstract val mode: String
    abstract val pid: String

    val rawCommand: String
        get() = listOf(mode, pid).joinToString(" ")

    open val handler: (RawResponse) -> String = { it.value }
}