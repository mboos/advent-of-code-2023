data class Mapping(val source: Long, val dest: Long, val count: Long) {
    fun contains(value: Long): Boolean {
        return value >= source && value < source + count
    }

    fun map(value: Long): Long {
        return value - source + dest
    }
}

fun getNext(source: Long, mappings: List<Mapping>): Long {
    var left = 0
    var right = mappings.size
    var index: Int = (left + right) / 2
    while (left < right) {
        val mapping = mappings[index]
        if (mapping.contains(source)) {
            return mapping.map(source)
        }
        if (mapping.source < source) {
            left = index + 1
        } else if (mapping.source > source) {
            right = index
        }
        index = (left + right) / 2
    }
    println("AAAH")
    return source
}

fun getNextRanges(start: Long, count: Long, mappings: List<Mapping>): List<Pair<Long, Long>> {
    // Skip the binary search for now in the interest of simplicity
    val foundRanges: MutableList<Pair<Long, Long>> = mutableListOf()
    mappings.forEach {
        // Cases: a contains all of b, a contains only start of b, a contains only end of b, b contains all of a
        val offset = it.source - start
        if (start <= it.source && it.source < start + count) {
            if (it.source + it.count - 1 < start + count) {  // a contains all of b
                foundRanges.add(Pair(it.dest, it.count))
            } else {  // a contains only start of b
                foundRanges.add(Pair(it.dest, count - offset))
            }
        } else if (start < it.source + it.count && it.source + it.count - 1 < start + count) {
            // a contains only end of b
            foundRanges.add(Pair(it.map(start), it.count + offset))
        } else if (it.source <= start && start < it.source + it.count) {
            // b contains all of a
            return listOf(Pair(it.map(start), count))
        }
    }
    return foundRanges
}

val attributes = listOf("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location")
val progressions = attributes.zipWithNext { a, b -> "${a}-to-${b}"}

fun loadEverything(input: List<String>): Pair<List<Long>, Map<String, MutableList<Mapping>>> {
    var section = "start"
    val seeds: List<Long> = "\\d+".toRegex().findAll(input[0]).map { it.value.toLong() }.toList()
    val mappings: Map<String, MutableList<Mapping>> = progressions.map<String, Pair<String, MutableList<Mapping>>> { it to mutableListOf() }.toMap()
    val mapPattern = "^(?<dest>\\d+) (?<source>\\d+) (?<count>\\d+)".toRegex()
    val sectionPattern = "\\w+-to-\\w+".toRegex()

    input.forEach {
        val newSectionMatch = sectionPattern.find(it)
        if (newSectionMatch != null) {
            section = newSectionMatch.value
            return@forEach
        }

        val mappingMatch = mapPattern.find(it)
        if (mappingMatch != null) {
            mappings[section]!!.add(Mapping(mappingMatch.groups["source"]!!.value.toLong(), mappingMatch.groups["dest"]!!.value.toLong(), mappingMatch.groups["count"]!!.value.toLong()))
        }
    }
    mappings.forEach {
        val mapping = it.value
        mapping.sortBy { element: Mapping -> element.source }

        val additions: MutableList<Mapping> = mutableListOf()
        mapping.zipWithNext {
            a, b -> 
            val startSrc = a.source + a.count
            if (startSrc < b.source) {
                additions.add(Mapping(startSrc, a.dest + a.count, b.source - startSrc))
            }
        }
        mapping.add(Mapping(mapping.last().source + mapping.last().count, mapping.last().source + mapping.last().count, seeds.sum()))
        if (mapping.first().source != 0.toLong()) {
            mapping.add(Mapping(0, 0, mapping.first().source))
        }
        mapping.addAll(additions)

        mapping.sortBy { element: Mapping -> element.source }
    }
    return Pair(seeds, mappings)
}

fun main() {
    fun part1(input: List<String>): Long {
        val (seeds, mappings) = loadEverything(input)

        var currentValues = seeds
        progressions.forEach {
            section ->
            currentValues = currentValues.map { getNext(it, mappings[section]!!) }
        }
        return currentValues.min()
    }

    fun part2(input: List<String>): Long {
        val (seeds, mappings) = loadEverything(input)

        var currentValues: List<Pair<Long, Long>> = seeds.zipWithNext().withIndex().filter { it.index % 2 == 0 }.map { it.value }
        progressions.forEach {
            section ->
            currentValues = currentValues.flatMap { 
                val (start, count) = it
                return@flatMap getNextRanges(start, count, mappings[section]!!) 
            }
        }
        return currentValues.map { 
            val (a,b) = it
            return@map a
        }.min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35.toLong())
    check(part2(testInput) == 46.toLong())

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
