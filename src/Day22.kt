import java.util.LinkedList
import java.util.PriorityQueue

data class Brick(val x1: Int, val y1: Int, val z1: Int, val x2: Int, val y2: Int, val z2: Int) : Comparable<Brick> {
    val minx: Int
        get() = if (x1 < x2) x1 else x2
    val miny: Int
        get() = if (y1 < y2) y1 else y2
    val minz: Int
        get() = if (z1 < z2) z1 else z2
    val maxx: Int
        get() = if (x1 > x2) x1 else x2
    val maxy: Int
        get() = if (y1 > y2) y1 else y2
    val maxz: Int
        get() = if (z1 > z2) z1 else z2
    
    val above: MutableSet<Brick> = mutableSetOf()
    val below: MutableSet<Brick> = mutableSetOf()

    override fun compareTo(other: Brick): Int {
        return minz.compareTo(other.minz)
    }

    fun drop(base: Int): Brick {
        val dropDist = minz - base - 1
        return Brick(x1, y1, z1-dropDist, x2, y2, z2-dropDist)
    }
}

fun readBricks(input: List<String>): List<Brick> {
    return input.map { line ->
        val firstCoords = line.split("~").component1().split(",").map { it.toInt() }
        val secondCoords = line.split("~").component2().split(",").map { it.toInt() }
        Brick(
            firstCoords.component1(), firstCoords.component2(), firstCoords.component3(),
            secondCoords.component1(), secondCoords.component2(), secondCoords.component3()
        )
    }
}

fun dropBricks(fallingBricks: List<Brick>): List<Brick> {
    val maxX = fallingBricks.maxOf { it.maxx }
    val maxY = fallingBricks.maxOf { it.maxy }
    val stackPattern = (0..maxX).map { (0..maxY).map { LinkedList<Brick>() }}

    return fallingBricks.sortedBy { it.minz }.map{
        brick ->
        val baseCandidates = (brick.minx..brick.maxx).flatMap { x->
            (brick.miny..brick.maxy).flatMap { y->
                if (stackPattern[x][y].isEmpty()) listOf() else listOf(stackPattern[x][y].getLast())
            }
        }
        val baseZ = baseCandidates.maxOfOrNull { it.maxz } ?: 0
        val dropped = brick.drop(baseZ)
        baseCandidates.filter { it.maxz == baseZ }.forEach { 
            it.above.add(dropped) 
            dropped.below.add(it)
        }
        (brick.minx..brick.maxx).forEach { x->
            (brick.miny..brick.maxy).forEach { y->
                stackPattern[x][y].offer(dropped)
            }
        }
        dropped
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val fallingBricks = readBricks(input)
        val droppedBricks = dropBricks(fallingBricks)

        println(droppedBricks.filter { it.above.all { a -> a.below.size > 1 } })
        return droppedBricks.count { it.above.all { a -> a.below.size > 1 } }
    }

    fun part2(input: List<String>): Int {
        val fallingBricks = readBricks(input)
        val droppedBricks = dropBricks(fallingBricks)

        val supporting = droppedBricks.map {
            brick ->
            val collapsing: MutableSet<Brick> = mutableSetOf(brick)
            val q = PriorityQueue<Brick>(brick.above)
            while (!q.isEmpty()) {
                val next = q.poll()
                if (next.below.all { collapsing.contains(it) }) {
                    q.addAll(next.above)
                    collapsing.add(next)
                }
            }
            collapsing -= brick
            collapsing
        }

        return supporting.map { it.size }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}
