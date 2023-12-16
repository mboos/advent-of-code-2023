fun chunk(input: List<String>): List<List<String>> {
    var start = 0
    val chunks: MutableList<List<String>> = mutableListOf()

    input.forEachIndexed {
        i, it ->
        if (it == "") {
            chunks.add(input.subList(start, i))
            start = i+1
        }
    }
    chunks.add(input.subList(start, input.size))
    return chunks
}

fun findHoriz(pattern: List<String>): Int {
    for (r in 0..pattern.size-2) {
        var matches = true
        var r1 = r
        var r2 = r+1
        while (r1 >= 0 && r2 < pattern.size) {
            if (pattern[r1] != pattern[r2]) {
                matches = false
                break
            }
            r1 -= 1
            r2 += 1
        }
        if (matches) {
            return r+1
        }
    }
    return 0
}

fun findSmudges(pattern: List<String>): Int {
    for (r in 0..pattern.size-2) {
        var smudges = 0
        var r1 = r
        var r2 = r+1
        while (r1 >= 0 && r2 < pattern.size) {
            smudges += pattern[r1].zip(pattern[r2]).map { (a, b) -> if (a!=b) 1 else 0 }.sum()
            if (smudges > 1) {
                break
            }
            r1 -= 1
            r2 += 1
        }
        if (smudges == 1) {
            return r+1
        }
    }
    return 0
}

fun rotate(pattern: List<String>): List<String> {
    return (0..pattern[0].length-1).map { i ->
        pattern.map { it[i] }.joinToString("")
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val chunks = chunk(input)
        var horizSplits = 0
        var vertSplits = 0
        for (pattern in chunks) {
            horizSplits += findHoriz(pattern)
            vertSplits += findHoriz(rotate(pattern))
        }
        return 100 * horizSplits + vertSplits
    }

    fun part2(input: List<String>): Int {
        val chunks = chunk(input)
        var horizSplits = 0
        var vertSplits = 0
        for (pattern in chunks) {
            horizSplits += findSmudges(pattern)
            vertSplits += findSmudges(rotate(pattern))
        }
        println("${horizSplits} ${vertSplits}")
        return 100 * horizSplits + vertSplits
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
