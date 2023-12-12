data class Coord(val r: Int, val c: Int) {
    fun distanceTo(coord: Coord, horiz: Set<Int>, vert: Set<Int>, factor: Int): Int {
        val (r1, r2) = if (r > coord.r) Pair(coord.r, r) else Pair(r, coord.r)
        val (c1, c2) = if (c > coord.c) Pair(coord.c, c) else Pair(c, coord.c)
        val x = c2 - c1
        val y = r2 - r1
        val h = horiz.filter { r1 < it && it < r2 }.size * (factor-1)
        val v = vert.filter { c1 < it && it < c2 }.size * (factor-1)

        return x + y + h + v
    }
}

fun compute(input: List<String>, factor: Int): Long {
    val horiz: MutableSet<Int> = mutableSetOf()
        val vert: MutableSet<Int> = mutableSetOf()
        val coords: MutableList<Coord> = mutableListOf()

        for (r in 0..input.size-1) {
            var found = false
            for (c in 0..input[0].length-1) {
                if (input[r][c] == '#') {
                    found = true
                    coords.add(Coord(r,c))
                }
            }
            if (!found) {
                horiz.add(r)
            }
        }
        for (c in 0..input[0].length-1) {
            var found = false
            for (r in 0..input.size-1) {
                if (input[r][c] == '#') {
                    found = true
                    break
                }
            }
            if (!found) {
                vert.add(c)
            }
        }

        var total = 0.toLong()
        for (i in 0..coords.size-2) {
            for (j in i+1..coords.size-1) {
                val dist = coords[i].distanceTo(coords[j], horiz, vert, factor)
                //println("${i+1} ${j+1} ${dist}")
                total += dist.toLong()
            }
        }
        return total
}

fun main() {
    fun part1(input: List<String>): Int {
        return compute(input, 2).toInt()
    }

    fun part2(input: List<String>): Long {
        return compute(input, 1000000)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
