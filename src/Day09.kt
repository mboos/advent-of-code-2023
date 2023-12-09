fun nextNum(numbers: List<Int>): Int {
    if (numbers.all { it == 0 }) {
        return 0
    }
    val diffs = numbers.zipWithNext { a, b -> b - a }
    return numbers.last() + nextNum(diffs)
}

fun prevNum(numbers: List<Int>): Int {
    if (numbers.all { it == 0 }) {
        return 0
    }
    val diffs = numbers.zipWithNext { a, b -> b - a }
    return numbers.first() - prevNum(diffs)
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.map {
            val numbers = it.split(" ").map { it.toInt() }

            nextNum(numbers)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.map {
            val numbers = it.split(" ").map { it.toInt() }

            prevNum(numbers)
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
