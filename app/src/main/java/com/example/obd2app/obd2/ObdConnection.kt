package com.example.obd2app.obd2

import java.io.InputStream
import java.io.OutputStream

class ObdConnection(
    private val inputStream: InputStream,
    private val outputStream: OutputStream
) {

}