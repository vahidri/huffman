package io

import java.io.FileInputStream
import java.io.File

/**
 * read compressed file
 */
class CompressedFileReader(file: File) {
    val inputStream = FileInputStream(file)

    var buffer: ByteArray

    var binaryString = ""

    var index = 0

    /**
     * reads all file and keep it on buffer
     * its better to read from file when Huffman needs it but this way was so simple from that one!
     */
    init {
        buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
    }

    /**
     * returns size of file or buffer
     */
    fun size() = buffer.size

    /**
     * converting a byte to string of 0 and 1
     * for example for a byte that contains character 'a' the return value will be 97 in binary or '01100001'
     */
    private fun byteToString(it: Byte): String {
        return String.format("%8s", Integer.toBinaryString(0xff and (it.toInt()))).replace(' ', '0')
    }

    /**
     * converts a byte to binary string and then returns first character of the binary string
     * then removes returned character from binary string
     * so next time calling read() this will return second character of the binary string
     * binary String will be updated if it becomes empty
     *
     * returns null if file has been ended
     */
    fun read(): Char? {
        if (binaryString.isEmpty() && index != size()) {
            buffer[index].apply { binaryString = byteToString(this) }
            index++
        }

        if (binaryString.isEmpty()) {
            return null
        }
        val ret = binaryString[0]
        binaryString = binaryString.substring(1)
        return ret
    }

    /**
     * reads first part of file that contains list of characters and their frequency and returns it as a list
     * list has size of 256 and each index is correspond with character that has same binary value
     */
    fun readTable(eof: Char, seperator: Char): IntArray {
        var i = 0
        val arr = IntArray(256)
        var char = buffer[index].toChar()
        var str = ""
        while (char != eof) {
            if (char != seperator) {
                str += char
            } else {
                arr[i] = if (str.isEmpty()) 0 else str.toInt()
                str = ""
                i++
            }
            index++
            char = buffer[index].toChar()
        }
        arr[i] = if (str.isEmpty()) 0 else str.toInt()

        index++

        return arr
    }
}