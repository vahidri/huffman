import io.CompressedFileReader
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

/**
 * class for code and decode in huffman way!!
 * @param file is file to run algorithm on it
 */

class Huffman(val file: File) {

    /**
     * @param compressedFile is file to write compressed data on it
     */
    private val compressedFile = File(file.absolutePath + ".hmc")
    /**
     * @param decompressedFile is file to write decompressed data on it
     */
    private val decompressedFile = File(file.absolutePath + ".decoded.txt")

    /**
     * create files for compressed and decompressed
     */
    init {
        if (!compressedFile.exists())
            compressedFile.createNewFile()
        if (decompressedFile.exists())
            decompressedFile.createNewFile()
        println(file.name)
    }

    /**
     *  priority queue to save characters sorted with their frequency
     */
    private var nodes = PriorityQueue<Node> { o1, o2 -> if (o1.value < o2.value) -1 else 1 }
    private var codes = TreeMap<Char, String>()

    private var text = ""
    private var ASCII = IntArray(128)
    private var EOF: Char = Character.toChars(3)[0]
    private var tableEOF = '~'
    var seperator = ','


    /**
     * simulate encode and decode process on original file and compressed file
     */
    fun handleNewText(): Boolean {
        text = read(file)
        ASCII = IntArray(256)
        nodes.clear()
        codes.clear()

        //getting frequency of each character
        for (i in 0 until text.length)
            ASCII[text[i].toInt()]++

        ASCII[3]++
        //generating huffman tree
        prepareForEncode()

        //print for log
        //printCodes()

        //start encode
        encodeText()
        //clear nodes and table just for simulate decoding
        nodes.clear()
        codes.clear()
        //start decode
        decodeText()
        //compute similarity of files
        val similarity = FileCompariator(file, decompressedFile).compare()
        //print answers
        println("compress ratio is : ${((file.length().toDouble() / compressedFile.length().toDouble()))}")
        println("saving_space is : ${(1 - ((compressedFile.length().toDouble() / file.length().toDouble()))) * 100}")

        val decompressed = read(decompressedFile)

        println("Original file MD5:     ${md5(text)}")
        println("Decompressed file MD5: ${md5(decompressed)}")

        println("File is ${similarity * 100} percent same to decompressed file")

        return false


    }

    /**
     * generate md5 hash for inputString
     * @param text is string to generate md5 for
     */
    private fun md5(text: String): String {
        val digest = MessageDigest.getInstance("MD5")

        digest.update(text.toByteArray())

        val byteData = digest.digest()

        val sb = StringBuffer()
        for (i in byteData.indices)
            sb.append(Integer.toString((byteData[i] and 0xff.toByte()) + 0x100, 16).substring(1))
        return sb.toString()
    }

    /**
     * clear all lists and regenerates them with ASCII array
     */
    private fun prepareForEncode() {

        calculateCharIntervals(nodes, false)

        //nodes.add(Node((1 / text.length).toDouble(), EOF.toString()))
        buildTree(nodes)
        generateCodes(nodes.peek(), "", 0)
    }

    /**
     * decodes compressed file and writes the result on decompressed file
     */
    fun decodeText() {
        val reader = CompressedFileReader(compressedFile)
        val writer = FileOutputStream(decompressedFile)
        //loading huffman words frequency from file
        val newArr = reader.readTable(tableEOF, seperator)
        //print(Arrays.equals(ASCII, newArr))
        ASCII = newArr
        //generating huffman tree from data loaded from file

        prepareForEncode()

        //start decode
        var char = reader.read()
        while (char != null) {
            var tmpNode: Node? = nodes.peek()
            while (tmpNode!!.left != null && tmpNode.right != null && char != null) {
                //print(char)
                tmpNode = if (char == '1')
                    tmpNode.right
                else
                    tmpNode.left
                char = reader.read()
            }
            if (tmpNode.character.length == 1) {
                if (tmpNode.character == EOF.toString()) return
                writer.write(tmpNode.character[0].toInt())
            }
        }
    }

    /**
     * encodes file and writes the result on compressed file
     */
    fun encodeText() {
        val outputStream = io.FileWriter(compressedFile)
        val table = generateTableString()
        //print(table + " " + table.length)
        outputStream.normalWrite(table)
        for (i in 0 until text.length) {
            outputStream.write(codes[text[i]]!!)
        }
        outputStream.write(codes[EOF]!!)
        outputStream.write(codes[EOF]!!)
        outputStream.flushLast()
        outputStream.close()
    }

    /**
     * generating a string to write a first of the file
     * this part is using to recover ASCII table from the compressed file
     */
    private fun generateTableString(): String {
        var ret = ""
        if (ASCII[0] > 0) {
            ret += ASCII[0]
        }
        (1 until ASCII.size).forEach {
            ret += seperator
            if (ASCII[it] > 0) {
                ret += ASCII[it]
            }
        }
        ret += tableEOF
        return ret
    }

    /**
     * building huffman tree
     */
    fun buildTree(vector: PriorityQueue<Node>) {
        //println(vector.peek().value)
        val node = vector.find { it.value == vector.maxBy { it.value }!!.value }
        vector.remove(node)
        while (vector.size > 1)
            vector.add(Node(vector.poll(), vector.poll()))
        vector.add(Node(node!!, vector.poll()))
    }

    /**
     * print each character with its huffman code for debug
     */
    fun printCodes() {
        codes.keys.forEach { println("$it : ${codes[it]}") }
    }

    /**
     * calculating frequency percent of any character and adding it to priority queue
     */
    fun calculateCharIntervals(vector: PriorityQueue<Node>, printIntervals: Boolean) {

        for (i in ASCII.indices)
            if (ASCII[i] > 0) {
                vector.add(Node(ASCII[i] / (text.length * 1.0), Character.toChars(i)[0].toString()))
                if (printIntervals)
                    println("'" + i.toChar() + "' : " + ASCII[i] / (text.length * 1.0))
            }
    }

    /**
     * generating huffman code for each character
     */
    fun generateCodes(node: Node?, s: String, lastIndex: Int) {
        if (node != null) {
            if (node.right != null)
                generateCodes(node.right, s + "1", lastIndex)

            if (node.left != null)
                generateCodes(node.left, s + "0", lastIndex)

            if (node.left == null && node.right == null)
                codes[node.character[0]] = s
        }
    }
}