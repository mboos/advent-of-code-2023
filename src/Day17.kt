import 	java.util.PriorityQueue
import kotlin.math.abs

data class Vec(val dr: Int, val dc: Int) {
    fun dist(): Int {
        // Not actual magnitude, but good enough for this problem
        return abs(dr) + abs(dc)
    }

    fun dir(): Vec {
        if (dr == 0 && dc == 0) {
            return this // Hopefully we never encounter this case
        } else if (dr == 0) {
            return Vec(0, if (dc > 0) 1 else -1)
        } else if (dc == 0) {
            return Vec(if (dr > 0) 1 else -1, 0)
        } else {
            return Vec(if (dr > 0) 1 else -1, if (dc > 0) 1 else -1)
        }
    }

    fun left(): Vec {
        return Vec(-dc, dr)
    }
}

data class Position(val r: Int, val c: Int) {
    operator fun minus(decrement: Position): Vec {
        return Vec(r - decrement.r, c - decrement.c)
    }

    operator fun plus(increment: Vec): Position {
        return Position(r + increment.dr, c + increment.dc)
    }

    operator fun minus(decrement: Vec): Position {
        return Position(r - decrement.dr, c - decrement.dc)
    }
}

data class StackElement(
    val heatLoss: Int, 
    val position: Position, 
    val turningPoint: Position,
    val history: List<VisitedElement>) : Comparable<StackElement> {
    override fun compareTo(other: StackElement) = heatLoss - other.heatLoss
}

data class VisitedElement(val position: Position, val direction: Vec) {}

data class VisitedSegment(val position: Position, val turningPoint: Position) {}

fun printMap(input: List<String>, visited: Collection<VisitedElement>) {
    val blocks = input.map { StringBuilder().append(it) }

    visited.forEach {
        val (pos, dir) = it
        if (dir == Vec(1,0)) {
            blocks[pos.r][pos.c] = 'v'
        } else if (dir == Vec(0,1)) {
            blocks[pos.r][pos.c] = '>'
        } else if (dir == Vec(-1,0)) {
            blocks[pos.r][pos.c] = '^'
        } else if (dir == Vec(0,-1)) {
            blocks[pos.r][pos.c] = '<'
        }
    }
    blocks.forEach {
        println(it)
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val queue = PriorityQueue<StackElement>()
        val visited: MutableSet<VisitedSegment> = mutableSetOf()
        queue.add(StackElement(0, Position(1,0), Position(0,0), listOf()))
        queue.add(StackElement(0, Position(0,1), Position(0,0), listOf()))

        while (queue.peek() != null) {
            val (heatLoss, position, turningPoint, history) = queue.poll()
            if (position.r < 0 || position.c < 0 || position.r >= input.size || position.c >= input[0].length) {
                continue
            }
            val diff = position - turningPoint
            val dir = diff.dir()
            val current = VisitedSegment(position, turningPoint)
            if (visited.contains(current)) {
                continue
            }
            visited.add(current)
            val newHistory = history + listOf(VisitedElement(position, dir))
            val newHeatLoss = heatLoss + input[position.r][position.c].digitToInt()
            if (position.r == input.size - 1 && position.c == input[0].length -1) {
                //printMap(input, newHistory)
                return newHeatLoss
            }
            val left = dir.left()
            queue.add(StackElement(newHeatLoss, position+left, position, newHistory))
            queue.add(StackElement(newHeatLoss, position-left, position, newHistory))
            if (diff.dist() != 3) {
                queue.add(StackElement(newHeatLoss, position+dir, turningPoint, newHistory))
            }
        }
        // no route found (should not be possible unless map is one char wide)
        return Int.MAX_VALUE
    }

    fun part2(input: List<String>): Int {
        val queue = PriorityQueue<StackElement>()
        val visited: MutableSet<VisitedSegment> = mutableSetOf()
        queue.add(StackElement(0, Position(1,0), Position(0,0), listOf()))
        queue.add(StackElement(0, Position(0,1), Position(0,0), listOf()))

        while (queue.peek() != null) {
            val (heatLoss, position, turningPoint, history) = queue.poll()
            if (position.r < 0 || position.c < 0 || position.r >= input.size || position.c >= input[0].length) {
                continue
            }
            val diff = position - turningPoint
            val dir = diff.dir()
            val current = VisitedSegment(position, turningPoint)
            if (visited.contains(current)) {
                continue
            }
            visited.add(current)
            val newHistory = history + listOf(VisitedElement(position, dir))
            val newHeatLoss = heatLoss + input[position.r][position.c].digitToInt()
            if (position.r == input.size - 1 && position.c == input[0].length -1) {
                if (diff.dist() >= 4 && diff.dist() <= 10) {
                    printMap(input, newHistory)
                    return newHeatLoss
                }
            }
            val left = dir.left()
            if (diff.dist() >= 4){
                queue.add(StackElement(newHeatLoss, position+left, position, newHistory))
                queue.add(StackElement(newHeatLoss, position-left, position, newHistory))
            }
            if (diff.dist() < 10) {
                queue.add(StackElement(newHeatLoss, position+dir, turningPoint, newHistory))
            }
        }
        // no route found (should not be possible unless map is one char wide)
        return Int.MAX_VALUE
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
