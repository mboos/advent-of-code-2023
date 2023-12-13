val QMARK = "\\?".toRegex()

val cache: MutableMap<Pair<String, List<Int>>,Long> = mutableMapOf()

fun countPossibilities(candidate: String, counts: List<Int>): Long {
    val pruned = candidate.trim { it == '.' }
    val key = Pair(pruned, counts)
    if (cache.contains(key)) {
        return cache[key]!!
    }
    if (counts.size == 0 && !pruned.contains('#')) {
        cache[key] = 1
        return 1
    } else if (counts.size == 0 || pruned == "") {
        cache[key] = 0
        return 0
    }
    var firstIndex = pruned.indexOfFirst { it != '#' }
    if (firstIndex == -1) {
        firstIndex = pruned.length
    }
    var lastIndex = pruned.indexOf('.')
    if (lastIndex == -1) {
        lastIndex = pruned.length
    }
    if (counts.first() < firstIndex) {
        return 0
    } else if(counts.first() > lastIndex) {
        if (pruned.substring(0..lastIndex-1).contains('#')){
            cache[key] = 0
            return 0
        } else {
            cache[key] = countPossibilities(pruned.substring(lastIndex..pruned.length-1), counts)
            return cache[key]!!
        }
    } else if (firstIndex == lastIndex) {
        cache[key] = countPossibilities(pruned.substring(firstIndex..pruned.length-1), counts.subList(1, counts.size))
        return cache[key]!!
    } else {
        cache[key] =(countPossibilities(pruned.replaceFirst(QMARK, "#"), counts) 
                            + countPossibilities(pruned.replaceFirst(QMARK, "."), counts))
        return cache[key]!!
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        var total = 0L

        input.forEach {
            it: String ->
            val pattern = it.split(' ').first()
            val counts = it.split(' ').last().split(',').map { it.toInt() }
            val possibilities = countPossibilities(pattern, counts)
            total += possibilities
        }
        return total
    }

    fun part2(input: List<String>): Long {
        var total = 0L

        input.forEach {
            it: String ->
            val p = it.split(' ').first()
            val fullPattern = "${p}?${p}?${p}?${p}?${p}"
            val counts = it.split(' ').last().split(',').map { it.toInt() }
            val possibilities = countPossibilities(fullPattern, counts+counts+counts+counts+counts)
            total += possibilities
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
