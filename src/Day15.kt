fun getHash(input: String): Int {
    var hash = 0
    input.forEach {
        c ->
        hash += c.code
        hash *= 17
        hash = hash % 256
    }
    return hash
}

class Lens(val label: String, var focal: Int) {
    var next: Lens? = null
}

fun getLens(root: Lens?, label: String): Pair<Lens?, Lens?> {
    var prev: Lens? = null
    var node: Lens? = root
    while(node != null) {
        if (node.label == label) {
            break
        }
        prev = node
        node = node.next
    }
    return Pair(node, prev)
}

fun main() {
    fun part1(input: List<String>): Int {
        var total = 0

        input[0].split(",").forEach {
            total += getHash(it)
        }
        return total
    }

    fun part2(input: List<String>): Int {
        val pattern = "(?<label>[a-z]+)(=(?<focal>\\d+))?".toRegex()
        val boxes: MutableList<Lens?> = mutableListOf()
        (0..255).forEach {boxes.add(null)}
        pattern.findAll(input[0]).forEach {
            match ->
            val label = match.groups["label"]!!.value
            val hash = getHash(label)
            if (match.groups["focal"] != null) {
                val focal = match.groups["focal"]!!.value.toInt()
                val (node, prev) = getLens(boxes[hash], label)
                if (node == null) {
                    if (prev == null) {
                        boxes[hash] = Lens(label, focal)
                    } else {
                        prev.next = Lens(label, focal)
                    }
                } else {
                    node.focal = focal
                }
            } else {
                val (node, prev) = getLens(boxes[hash], label)
                if (node != null) {
                    if (prev == null) {
                        boxes[hash] = node.next
                    } else {
                        prev.next = node.next
                    }
                }
            }
        }

        var total = 0
        boxes.forEachIndexed {
            i, it ->
            var lens = 1
            var node = it
            while(node != null) {
                total += (i+1) * lens * node.focal
                lens += 1
                node = node.next
            }
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
