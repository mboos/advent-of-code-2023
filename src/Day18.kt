import java.util.LinkedList
import java.math.BigInteger

fun addEdge(x:Int, y:Int, edges:MutableMap<Int, MutableSet<Int>>) {
    if (!edges.contains(y)) {
        edges[y] = mutableSetOf()
    }
    edges[y]!!.add(x)
}

fun addEdge(x:Pair<Int, Int>, y:Pair<Int,Int>, edges:MutableMap<Pair<Int,Int>, MutableSet<Pair<Int,Int>>>) {
    if (!edges.contains(y)) {
        edges[y] = mutableSetOf()
    }
    edges[y]!!.add(x)
}

fun buildMap1(input: List<String>): MutableMap<Int, MutableSet<Int>> {
    val edges: MutableMap<Int, MutableSet<Int>> = mutableMapOf()
    var x = 0
    var y = 0
    input.forEach {
        val dir = it[0]
        val dist = it.split(" ").component2().toInt()
        if (dir == 'U') {
            for (dy in 1..dist) {
                addEdge(x,y+dy,edges)
            }
            y += dist
        } else if (dir == 'D') {
            for (dy in -dist..-1) {
                addEdge(x,y+dy,edges)
            }
            y -= dist
        } else if (dir == 'L') {
            for (dx in -dist..-1) {
                addEdge(x+dx,y,edges)
            }
            x -= dist
        } else if (dir == 'R') {
            for (dx in 1..dist) {
                addEdge(x+dx,y,edges)
            }
            x += dist
        } 
    }
    return edges
}

fun buildMap2(input: List<String>): Pair<MutableMap<Pair<Int,Int>, MutableSet<Pair<Int,Int>>>, MutableMap<Pair<Int,Int>, Char>> {
    val edges: MutableMap<Pair<Int,Int>, MutableSet<Pair<Int,Int>>> = mutableMapOf()
    val corners: MutableMap<Pair<Int,Int>, Char> = mutableMapOf()
    var x = 0
    var y = 0
    val ys: MutableSet<Int> = mutableSetOf(0)
    val moves: List<Pair<Char,Int>> = input.map {
        val colorCode = it.split(" ").component3()
        val dist = colorCode.substring(2,7).toInt(radix = 16)
        val dir = colorCode[7]
        if (dir == '3') {
            corners[Pair(x,y)] = dir
            y += dist
            ys.add(y)
            corners[Pair(x,y)] = dir
        } else if (dir == '1') {
            corners[Pair(x,y)] = dir
            y -= dist
            ys.add(y)
            corners[Pair(x,y)] = dir
        } else if (dir == '2') {
            x -= dist
        } else if (dir == '0') {
            x += dist
        } 
        Pair(dir, dist)
    }
    val yss = ys.sortedBy {it}

    var j = yss.indexOf(0)

    moves.forEach {
        (dir, dist) ->
        if (dir == '3') {   
            while (yss[j] < y + dist) {
                if (yss[j] != y) {
                    addEdge(Pair(x,x), Pair(yss[j], yss[j]), edges)
                }
                addEdge(Pair(x,x), Pair(yss[j]+1, yss[j+1]-1), edges)
                j += 1
            }
            y += dist
        } else if (dir == '1') {
            while (yss[j] > y - dist) {
                if (yss[j] != y) {
                    addEdge(Pair(x,x), Pair(yss[j], yss[j]), edges)
                }
                addEdge(Pair(x,x), Pair(yss[j-1]+1, yss[j]-1), edges)
                j -= 1
            }
            y -= dist
        } else if (dir == '2') {
            addEdge(Pair(x-dist, x), Pair(y,y), edges)
            x -= dist
        } else if (dir == '0') {
            addEdge(Pair(x, x+dist), Pair(y,y), edges)
            x += dist
        } 
    }
    return Pair(edges,corners)
}

fun measureHole(edges: MutableMap<Int, MutableSet<Int>>): Int {
    var total = 0
    edges.forEach { (y, xs) ->
        total += xs.size
        val adjacents = LinkedList<Int>()
        var inside = false
        xs.sortedBy { it }.zipWithNext().forEach {
            (a, b) ->
            if (a + 1 == b) {
                if (adjacents.peek() != a) {
                    adjacents.push(a)
                }
                adjacents.push(b)
            } else {
                if (!adjacents.isEmpty()) {
                    val first = adjacents.peekLast()
                    val last = adjacents.peekFirst()
                    adjacents.clear()
                    if (edges.getOrDefault(y-1, setOf()).contains(first) == edges.getOrDefault(y-1, setOf()).contains(last)) {
                        inside = !inside
                    }
                }
                inside = !inside
                if (inside) {
                    total += b - a - 1
                }
            }
        }
    }
    return total
}

fun measureBiggerHole(details: Pair<MutableMap<Pair<Int,Int>, MutableSet<Pair<Int,Int>>>, MutableMap<Pair<Int,Int>, Char>>): BigInteger {
    var (edges, corners) = details
    var total = BigInteger.valueOf(0)
    edges.forEach { (y, xs) ->
        val dy = BigInteger.valueOf((y.second - y.first + 1).toLong())
        val dx = BigInteger.valueOf(xs.map { it.second - it.first + 1 }.sum().toLong())
        total += dy * dx
        var inside = false
        xs.sortedBy { it.first }.zipWithNext().forEach {
            (a, b) ->
            
            var corner1 = Pair(a.first, y.first)
            var corner2 = Pair(a.second, y.first)
            if ((!corners.contains(corner1) || !corners.contains(corner2)) || corners[corner1] == corners[corner2]) {
                inside = !inside
            }
            
            if (inside) {
                val dx2 = BigInteger.valueOf((b.first - a.second - 1).toLong())
                total += dy * dx2
            }
        }
    }
    return total
}

fun main() {
    fun part1(input: List<String>): Int {
        return measureHole(buildMap1(input))
    }

    fun part2(input: List<String>): BigInteger {
        return  measureBiggerHole(buildMap2(input))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62)
    check(part2(testInput) == BigInteger.valueOf(952408144115L))

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}
