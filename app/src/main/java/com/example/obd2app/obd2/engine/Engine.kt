package com.example.obd2app.obd2.engine

import com.example.obd2app.obd2.ObdCommand
import com.example.obd2app.obd2.command.RawResponse

class SpeedCommand : ObdCommand() {
    override val mode = "01"
    override val pid = "0D"

//    override val handler = { it: RawResponse -> (bytesToInt(it.bufferedValue) / 4).toString() }
    override val handler = { it: RawResponse -> it.value }
}