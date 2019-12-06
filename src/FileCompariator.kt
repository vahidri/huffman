import java.io.File
import java.io.FileInputStream

/**
 * compare two files character by character
 */
class FileCompariator(val file1: File, val file2: File) {
    val is1 = FileInputStream(file1)
    val is2 = FileInputStream(file2)

    val size = Math.max(is1.available(), is2.available())

    val minSize = Math.min(is1.available(), is2.available())

    var sameChar = 0


    /**
     * moving in two files at same time and counting same characters
     * returns same character count divided by max size of two files
     */

    fun compare(): Float {
        var char1 = is1.read()
        var char2 = is2.read()

        while (char1 != -1 && char2 != -1) {
            if (char1 == char2) sameChar++
            char1 = is1.read()
            char2 = is2.read()
        }

        return sameChar.toFloat() / size.toFloat()
    }
}