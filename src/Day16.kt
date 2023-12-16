import java.util.LinkedList

data class Vector(val r: Int, val c: Int) {
    operator fun plus(increment: Vector): Vector {
        return Vector(r+increment.r, c+increment.c)
    }
}

data class HistoryElement(val position: Vector, val direction: Vector) {
}

fun beamCount(input: List<String>, startingPoint: HistoryElement): Int {
    val history: MutableSet<HistoryElement> = mutableSetOf()
    val locations: MutableSet<Vector> = mutableSetOf()
    val stack = LinkedList<HistoryElement>()
    stack.push(startingPoint)

    while(!stack.isEmpty()) {
        val current = stack.pop()
        if (history.contains(current)) {
            continue
        }
        history.add(current)
        val (pos, dir) = current
        val (r,c) = pos
        val (dr, dc) = dir
        if (r < 0 || c < 0 || r >= input.size || c >= input[0].length) {
            continue
        }
        locations.add(pos)
        if (input[r][c] == '.') {
            stack.push(HistoryElement(pos+dir, dir))
        } else if (input[r][c] == '\\') {
            val newDir = Vector(dc, dr)
            stack.push(HistoryElement(pos+newDir, newDir))
        } else if (input[r][c] == '/') {
            val newDir = Vector(-dc, -dr)
            stack.push(HistoryElement(pos+newDir, newDir))
        } else if (input[r][c] == '|') {
            if (dc == 0) {
                stack.push(HistoryElement(pos+dir, dir))
            } else {
                val dir1 = Vector(1,0)
                val dir2 = Vector(-1,0)
                stack.push(HistoryElement(pos+dir1, dir1))
                stack.push(HistoryElement(pos+dir2, dir2))
            }
        } else if (input[r][c] == '-') {
            if (dr == 0) {
                stack.push(HistoryElement(pos+dir, dir))
            } else {
                val dir1 = Vector(0, 1)
                val dir2 = Vector(0, -1)
                stack.push(HistoryElement(pos+dir1, dir1))
                stack.push(HistoryElement(pos+dir2, dir2))
            }
        }
    }

    return locations.size
}

fun main() {
    fun part1(input: List<String>): Int {
        return beamCount(input, HistoryElement(Vector(0,0), Vector(0,1)))
    }

    fun part2(input: List<String>): Int {
        val startingPoints: MutableList<HistoryElement> = mutableListOf()
        startingPoints.addAll((0..input.size-1).map { HistoryElement(Vector(it, 0), Vector(0, 1))})
        startingPoints.addAll((0..input.size-1).map { HistoryElement(Vector(it, input[0].length-1), Vector(0, -1))})
        startingPoints.addAll((0..input[0].length-1).map { HistoryElement(Vector(0, it), Vector(1, 0))})
        startingPoints.addAll((0..input[0].length-1).map { HistoryElement(Vector(input.size-1, it), Vector(-1, 0))})

        return startingPoints.map { beamCount(input, it) }.max()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
