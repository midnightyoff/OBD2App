package com.example.obd2app.obd2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class ObdConnection(
    private val inputStream: InputStream,
    private val outputStream: OutputStream
) {
    suspend fun query(command: String): ObdResponse = runBlocking {
        withContext(Dispatchers.IO) {
            outputStream.write("${command}\r".toByteArray())
            outputStream.flush()

            val data = readData()
            ObdResponse(data)
        }
    }

    private suspend fun readData(maxRetries: Int = 1): String = runBlocking {
        var b: Byte
        var c: Char
        val res = StringBuffer()
        var retriesCount = 0

        withContext(Dispatchers.IO) {
            // read until '>' arrives OR end of stream reached (-1)
            while (retriesCount <= maxRetries) {
                if (inputStream.available() > 0) {
                    b = inputStream.read().toByte()
                    if (b < 0) {
                        break
                    }
                    c = b.toInt().toChar()
                    if (c == '>') {
                        break
                    }
                    res.append(c)
                } else {
                    retriesCount += 1
                    delay(500)
                }
            }
            res.toString()
//            removeAll(SEARCHING_PATTERN, res.toString()).trim()
        }
    }
    private  suspend fun send(command: String) {

    }
    private suspend fun send(command: ObdCommand, delayTime: Long) = runBlocking {
        withContext(Dispatchers.IO) {
            outputStream.write("${command.rawCommand}\r".toByteArray())
            outputStream.flush()
            if (delayTime > 0) {
                delay(delayTime)
            }
        }
    }
}