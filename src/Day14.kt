fun tiltNorth(input: List<String>): List<String> {
    val platform = input.map { StringBuilder().append(it) }

    for (c in 0..platform[0].length-1) {
        var r1 = 0
        while(r1 < platform.size) {
            var count = 0
            var r2 = r1
            while (r2 < platform.size && platform[r2][c] != '#') {
                if (platform[r2][c] == 'O') {
                    count += 1
                }
                r2 += 1
            }
            for (r3 in r1..r1+count-1) {
                platform[r3][c] = 'O'
            }
            for (r4 in r1+count..r2-1) {
                platform[r4][c] = '.'
            }
            r1 = r2+1
        }
    }
    return platform.map { it.toString() }
}

fun tiltSouth(input: List<String>): List<String> {
    val platform = input.map { StringBuilder().append(it) }

    for (c in 0..platform[0].length-1) {
        var r1 = platform.size-1
        while(r1 >= 0) {
            var count = 0
            var r2 = r1
            while (r2 >= 0 && platform[r2][c] != '#') {
                if (platform[r2][c] == 'O') {
                    count += 1
                }
                r2 -= 1
            }
            for (r3 in r1-count+1..r1) {
                platform[r3][c] = 'O'
            }
            for (r4 in r2+1..r1-count) {
                platform[r4][c] = '.'
            }
            r1 = r2-1
        }
    }
    return platform.map { it.toString() }
}

fun tiltWest(input: List<String>): List<String> {
    val platform = input.map { StringBuilder().append(it) }

    for (r in 0..platform.size-1) {
        var c1 = 0
        while(c1 < platform[r].length) {
            var count = 0
            var c2 = c1
            while (c2 < platform[r].length && platform[r][c2] != '#') {
                if (platform[r][c2] == 'O') {
                    count += 1
                }
                c2 += 1
            }
            for (c3 in c1..c1+count-1) {
                platform[r][c3] = 'O'
            }
            for (c4 in c1+count..c2-1) {
                platform[r][c4] = '.'
            }
            c1 = c2+1
        }
    }
    return platform.map { it.toString() }
}

fun tiltEast(input: List<String>): List<String> {
    val platform = input.map { StringBuilder().append(it) }

    for (r in 0..platform.size-1) {
        var c1 = platform[r].length - 1
        while(c1 >= 0) {
            var count = 0
            var c2 = c1
            while (c2 >= 0 && platform[r][c2] != '#') {
                if (platform[r][c2] == 'O') {
                    count += 1
                }
                c2 -= 1
            }
            for (c3 in c1-count+1..c1) {
                platform[r][c3] = 'O'
            }
            for (c4 in c2+1..c1-count) {
                platform[r][c4] = '.'
            }
            c1 = c2-1
        }
    }
    return platform.map { it.toString() }
}

val mycache: MutableMap<List<String>,List<String>> = mutableMapOf()
val countcache: MutableMap<List<String>, Long> = mutableMapOf()

fun cycle(platform: List<String>): List<String> {
    if (!mycache.contains(platform)) {
        mycache[platform] = tiltEast(tiltSouth(tiltWest(tiltNorth(platform))))
    }
    if (platform == mycache[platform]) {
        println("!!!")
    }
    return mycache[platform]!!
}

fun main() {
    fun part1(input: List<String>): Int {
        val pattern = tiltNorth(input)
        var total = 0
        pattern.forEachIndexed {
            i, line ->
            total += (pattern.size - i) * line.count { it == 'O' }
        }

        return total
    }

    fun part2(input: List<String>): Int {
        var pattern = input
        var i = 0L
        while(i < 1000000000) {
            i += 1
            pattern = cycle(pattern)
            if (countcache.contains(pattern)) {
                break
            }
            countcache[pattern] = i
        }
        val cycleSize = i - countcache[pattern]!!
        val remaining = 1000000000 - i
        val remainder = remaining % cycleSize
        for (j in 1..remainder) {
            pattern = cycle(pattern)
        }
        var total = 0
        pattern.forEachIndexed {
            i, line ->
            total += (pattern.size - i) * line.count { it == 'O' }
        }

        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
