class Node (var copies: Int) {
    private var _next: Node? = null
    val next: Node
        get() {
            if (_next == null) {
                _next = Node(1)
            }
            return _next ?: throw AssertionError("Spooky")
        }

    fun makeCopies(wins: Int, amount: Int = copies) {
        if (wins <= 0) {
            return
        }
        next.makeCopies(wins - 1, amount)
        next.copies += amount
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        var total = 0
        val pattern = "(?<winning>(\\d+\\s*?)+)\\s+\\|\\s+(?<scratch>(\\d+\\s*?)+)$".toRegex()
        input.forEach {
            val match = pattern.find(it)
            val winningNumbers = match!!.groups["winning"]!!.value.split("\\s+".toRegex()).map { it.toInt() }.toSet()
            val scratchNumbers = match.groups["scratch"]!!.value.split("\\s+".toRegex()).map { it.toInt() }.toSet()

            val commonNumbers = winningNumbers.intersect(scratchNumbers)
            if (!commonNumbers.isEmpty()) {
                total += (1 shl (commonNumbers.size - 1))
            }
        }
        return total
    }

    fun part2(input: List<String>): Int {
        var total = 0
        val pattern = "(?<winning>(\\d+\\s*?)+)\\s+\\|\\s+(?<scratch>(\\d+\\s*?)+)$".toRegex()
        var node = Node(1)
        input.forEach {
            val match = pattern.find(it)
            val winningNumbers = match!!.groups["winning"]!!.value.split("\\s+".toRegex()).map { it.toInt() }.toSet()
            val scratchNumbers = match.groups["scratch"]!!.value.split("\\s+".toRegex()).map { it.toInt() }.toSet()

            val commonNumbers = winningNumbers.intersect(scratchNumbers)
            node.makeCopies(commonNumbers.size)
            total += node.copies
            node = node.next
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
