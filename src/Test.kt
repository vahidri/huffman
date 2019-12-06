import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

/**
 * main function of application
 */
fun main(args: Array<String>) {
//    path is set by default
    if (args.isEmpty() || args.size != 1) {
        print("usage : java -jar file.jar <PATH TO FILE>")
        return
    }
    val file = File(args[0])
    val list = ArrayList<File>()
    loadFiles(file, list)
    list.forEach {
        try {
            Huffman(it).handleNewText()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println()
    }

}

/**
 * reads content of a file and return it as String
 */
fun read(file: File): String {
    val inputStream = FileInputStream(file)
    val bytes = ByteArray(inputStream.available())
    inputStream.read(bytes)
    return String(bytes, Charset.forName("ISO-8859-1"))
}

/**
 * load all files in a folder recursively into a list
 *
 * @param file is a directory or a file
 * @param list to insert files to it
 */
fun loadFiles(file: File, list: ArrayList<File>) {
    if (file.isDirectory) {
        file.listFiles().forEach { loadFiles(it, list) }
    } else {
        list.add(file)
    }
}
