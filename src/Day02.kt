fun main() {
    fun part1(input: List<String>): Int {
        // 12 red cubes, 13 green cubes, and 14 blue cubes
        val maxCount = mapOf("red" to 12, "green" to 13, "blue" to 14)
        val pattern = "(?<num>\\d+) (?<colour>red|green|blue)".toRegex()
        var total = 0

        input.forEachIndexed {
            i, line ->
            var match = pattern.find(line)

            while(match != null) {
                val colour = match.groups["colour"]?.value
                val num = match.groups["num"]?.value?.toInt()!!
                if (maxCount[colour]!! < num) {
                    return@forEachIndexed
                }
                match = match.next()
            }
            total += i + 1
        }
        return total
    }

    fun part2(input: List<String>): Int {
        val pattern = "(?<num>\\d+) (?<colour>red|green|blue)".toRegex()
        var total = 0

        input.forEach {
            line ->
            var maxCount = mutableMapOf("red" to 0, "green" to 0, "blue" to 0)
            var match = pattern.find(line)

            while(match != null) {
                val colour = match!!.groups["colour"]?.value!!
                val num = match!!.groups["num"]?.value?.toInt()!!
                if (maxCount[colour]!! < num) {
                    maxCount[colour] = num
                }
                match = match!!.next()
            }
            total += maxCount["red"]!! * maxCount["green"]!! * maxCount["blue"]!!
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
