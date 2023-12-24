import java.util.PriorityQueue
import java.util.Stack

data class Walk(val position: Coord, val visited: Set<Coord>): Comparable<Walk> {
    override fun compareTo(other: Walk): Int {
        return visited.size.compareTo(other.visited.size)
    }
}

data class LongWalk(val position: Coord, val visited: Set<Coord>, val distance: Int): Comparable<LongWalk> {
    override fun compareTo(other: LongWalk): Int {
        return distance.compareTo(other.distance)
    }
}

fun longestPath(start: Coord, end: Coord, input: List<String>, slopes:Boolean = true): Int {
    lateinit var longest: Walk

    val q = PriorityQueue<Walk>()
    q.add(Walk(start, setOf()))

    while (!q.isEmpty()) {
        val current = q.poll()

        if (current.position == end) {
            longest = current
            continue
        }
        val (r,c) = current.position
        if (r < 0 || c < 0) {
            continue
        }
        val tileType = input[r][c]
        if (current.visited.contains(current.position) || tileType == '#') {
            continue
        }
        val newVisited = current.visited + setOf(current.position)
        if (slopes && tileType == '>') {
            q.add(Walk(Coord(r,c+1), newVisited))
        } else if (slopes && tileType == '<') {
            q.add(Walk(Coord(r,c-1), newVisited))
        } else if (slopes && tileType == '^') {
            q.add(Walk(Coord(r-1,c), newVisited))
        } else if (slopes && tileType == 'v') {
            q.add(Walk(Coord(r+1,c), newVisited))
        } else {
            q.add(Walk(Coord(r,c+1), newVisited))
            q.add(Walk(Coord(r,c-1), newVisited))
            q.add(Walk(Coord(r-1,c), newVisited))
            q.add(Walk(Coord(r+1,c), newVisited))
        }
    }

    return longest.visited.size
}

fun getNext(current: Coord, last: Coord?, input: List<String>): Set<Coord> {
    val neighbours: MutableSet<Coord> = mutableSetOf()
    val (r,c) = current
    if (r > 0 && input[r-1][c] != '#') {
        neighbours += Coord(r-1, c)
    }
    if (r < input.lastIndex && input[r+1][c] != '#') {
        neighbours += Coord(r+1, c)
    }
    if (input[r][c-1] != '#') {
        neighbours += Coord(r, c-1)
    }
    if(input[r][c+1] != '#') {
        neighbours += Coord(r, c+1)
    }
    if (last != null) {
        neighbours -= last
    }
    return neighbours
}

fun simplifyGraph(input: List<String>): Map<Coord, Map<Coord, Int>> {
    val graph: MutableMap<Coord, MutableMap<Coord, Int>> = mutableMapOf()
    val visited: MutableSet<Coord> = mutableSetOf()
    val end = Coord(input.lastIndex, input[0].lastIndex-1)
    
    val stack = Stack<Coord>()
    stack.push(Coord(0,1))

    while (!stack.isEmpty()) {
        val start = stack.pop()
        if (visited.contains(start)) {
            continue
        }
        getNext(start, null, input).forEach {
            next ->
            if (!visited.contains(next)) {
                var distance = 1
                var nextNext = next
                var last = start
                var neighbours = getNext(nextNext, last, input) 
                //println("${nextNext} -> ${neighbours}")
                while(neighbours.size == 1) {
                    visited.add(nextNext)
                    distance += 1
                    last = nextNext
                    nextNext = neighbours.first()
                    neighbours = getNext(nextNext, last, input)
                }
                // This check is unnecesary, since it doesn't seem we have any dangling ends
                if (neighbours.size > 0 || nextNext == end) {
                    val startMap = graph.getOrPut(start) {mutableMapOf()}
                    if (startMap.getOrDefault(nextNext,0) < distance) {
                        startMap[nextNext] = distance
                        graph.getOrPut(nextNext) {mutableMapOf()} [start] = distance
                        stack.push(nextNext)
                    }
                }
            }
        }
        visited.add(start)
    }
    return graph
}

fun longerestPath(start: Coord, end: Coord, graph: Map<Coord, Map<Coord,Int>>): Int {
    var longest: LongWalk? = null

    val q = Stack<LongWalk>()
    q.push(LongWalk(graph[start]!!.keys.first(), setOf(), 0))
    val fakeEnd = graph[end]!!.keys.first()

    val baseDistance = graph[start]!!.values.first() + graph[end]!!.values.first()

    while (!q.isEmpty()) {
        val current = q.pop()

        
        if (current.visited.contains(current.position)) {
            continue
        }
        if (current.position == fakeEnd && (longest == null || longest.distance < current.distance)) {
            println("${current.distance} ${baseDistance}")
            longest = current
            continue
        }
        //println("${current.position} ${current.distance}")
        val newVisited = current.visited + setOf(current.position)
        q.addAll(graph[current.position]!!.map {
            (next: Coord, distance: Int) ->
            LongWalk(next, newVisited, current.distance + distance)
        })
    }
   
    return longest!!.distance + baseDistance
}

fun main() {
    fun part1(input: List<String>): Int {
        val start = Coord(0, 1)
        val end = Coord(input.lastIndex, input[0].lastIndex - 1)

        return longestPath(start,end,input)
    }

    fun part2(input: List<String>): Int {
        val start = Coord(0, 1)
        val end = Coord(input.lastIndex, input[0].lastIndex - 1)

        val graph = simplifyGraph(input)
        println(graph.map { (k,v) -> v.size }.sum())
        return longerestPath(start, end, graph)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94)
    check(part2(testInput) == 154)

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}
