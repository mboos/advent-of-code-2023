fun main() {
    fun part1(input: List<String>): Int {
        val numPattern: Regex = "[0-9]+".toRegex()
        val symbolPattern: Regex = "[^0-9.]".toRegex()
        var total = 0
        for (i in 0 until input.size) {
            val line: String = input[i]
            var numMatch = numPattern.find(line)

            while(numMatch != null) {
                val range = numMatch.range
                val startI = if (i > 0)  i - 1 else i
                val endI = if (i < input.size - 1)  i + 1 else i
                val startJ = if (range.start > 0)  range.start - 1 else range.start
                val endJ = if (range.endInclusive < line.length - 1)  range.endInclusive + 2 else range.endInclusive + 1
                
                for(k in startI until endI+1) {
                    if(input[k].substring(startJ, endJ).contains(symbolPattern)) {
                        total += numMatch.value.toInt()
                        continue
                    }
                }

                numMatch = numMatch.next()
            }
        }
        return total
    }

    fun part2(input: List<String>): Int {
        val numPattern: Regex = "[0-9]+".toRegex()
        val starPattern: Regex = "[*]".toRegex()
        var total = 0
        for (i in 0 until input.size) {
            val line: String = input[i]
            var starMatch = starPattern.find(line)
            while(starMatch != null) {
                val index = starMatch.range.start
                val startI = if (i > 0)  i - 1 else i
                val endI = if (i < input.size - 1)  i + 1 else i
                val rangeJ = IntRange(
                    if (index > 0)  index - 1 else index,
                    if (index < line.length - 1) index + 1 else index
                ) 
                
                val numbers: MutableList<Int> = mutableListOf()
                for(k in startI until endI+1) {
                    for(numberMatch in numPattern.findAll(input[k])) {
                        if (numberMatch.range.intersect(rangeJ).size > 0) {
                            numbers.add(numberMatch.value.toInt())
                        }
                    }
                }
                if (numbers.size == 2) {
                    total += numbers[0] * numbers[1]
                }

                starMatch = starMatch.next()
            }
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
