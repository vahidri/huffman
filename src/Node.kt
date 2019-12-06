/**
 * node of the huffman tree
  */
class Node {
    var left: Node? = null
    var right: Node? = null
    var value: Double = 0.toDouble()
    var character: String

    constructor(value: Double, character: String) {
        this.value = value
        this.character = character
        left = null
        right = null
    }

    constructor(left: Node, right: Node) {
        this.value = left.value + right.value
        character = left.character + right.character
        if (left.value < right.value) {
            this.right = right
            this.left = left
        } else {
            this.right = left
            this.left = right
        }
    }
}