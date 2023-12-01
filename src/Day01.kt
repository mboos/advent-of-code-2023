fun main() {
    fun part1(input: List<String>): Int {
        var total = 0
        val pattern = "^[^0-9]*(?<first>[0-9])(.*(?<second>[0-9]))?[^0-9]*".toRegex()

        input.forEach { 
            val match = pattern.matchEntire(it)
            val digit1 = match!!.groups["first"]?.value
            val digit2 = match.groups["second"]?.value ?: digit1
            total += digit1!!.toInt() * 10 + digit2!!.toInt()
        }
        return total
    }

    fun part2(input: List<String>): Int {
        var total = 0
        val pattern = "(?=([0-9]|one|two|three|four|five|six|seven|eight|nine))".toRegex()
        val numberMap = mapOf(
            "one" to 1, "1" to 1,
            "two" to 2, "2" to 2,
            "three" to 3, "3" to 3,
            "four" to 4, "4" to 4,
            "five" to 5, "5" to 5,
            "six" to 6, "6" to 6,
            "seven" to 7, "7" to 7,
            "eight" to 8, "8" to 8,
            "nine" to 9, "9" to 9,
            "0" to 0,
        )

        input.forEach { 
            val first = it.findAnyOf(numberMap.keys)?.second
            val last = it.findLastAnyOf(numberMap.keys)?.second

            total += numberMap[first]!! * 10 + numberMap[last]!!
        }
        return total

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val testInput2 = readInput("Day01_test_b")
    check(part2(testInput2) == 88)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
