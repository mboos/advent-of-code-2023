val stepCache: MutableMap<Pair<Coord, Int>, Set<Coord>> = mutableMapOf()
fun endPoints(start: Coord, stepsLeft: Int, input: List<String>): Set<Coord> {
    if (stepsLeft == 0) {
        return setOf(start)
    }
    val key = Pair(start, stepsLeft)
    if (!stepCache.contains(key)) {
        val (r, c) = start
        val points: MutableSet<Coord> = mutableSetOf()
        if (getVal(Coord(r-1, c), input) != '#') {
            points.addAll(endPoints(Coord(r-1, c), stepsLeft - 1, input))
        }
        if (getVal(Coord(r, c-1), input) != '#') {
            points.addAll(endPoints(Coord(r, c-1), stepsLeft - 1, input))
        }
        if (getVal(Coord(r+1, c), input) != '#') {
            points.addAll(endPoints(Coord(r+1, c), stepsLeft - 1, input))
        }
        if (getVal(Coord(r, c+1), input) != '#') {
            points.addAll(endPoints(Coord(r, c+1), stepsLeft - 1, input))
        }
        stepCache[key] = points
    }
    return stepCache[key]!!
}

fun getVal(coord: Coord, input: List<String>): Char {
    return input[(coord.r+26501365) % input.size][(coord.c+26501365) % input[0].length]
}

fun findS(input: List<String>): Coord {
    input.forEachIndexed {
        r, it ->
        val c = it.indexOf('S')
        if (c >= 0) {
            return Coord(r, c)
        }
    }
    return Coord(-1, -1)
}

fun main() {
    fun part1(input: List<String>): Int {
        val s = findS(input)
        val points = endPoints(s, 64, input)
        input.forEachIndexed {
            r, line ->
            line.forEachIndexed {
                c, v -> print(if (points.contains(Coord(r,c))) '0' else v)
            }
            println()
        }
        return points.size
    }

    fun part2(input: List<String>): Int {
        val s = findS(input)
        return endPoints(s, 26501365, input).size
    }

    // test if implementation meets criteria from the description, like:
    // val testInput = readInput("Day01_test")
    // check(part1(testInput) == 1)

    val input = readInput("Day21")
    part1(input).println()
    part2(input).println()
}
