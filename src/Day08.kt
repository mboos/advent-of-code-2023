class DirectionNode(val direction: Char) {
    var next: DirectionNode? = null
}

fun loadInput(input: List<String>): Pair<DirectionNode, MutableMap<String, Pair<String, String>>> {
    val (_,mapInput) = input.partition { !it.contains("=")}
    var firstNode: DirectionNode? = null
    var lastNode: DirectionNode? = null

    for(it in input[0]) {
        val newNode = DirectionNode(it)
        if (lastNode == null) {
            firstNode = newNode
        } else {
            lastNode.next = newNode
        }
        lastNode = newNode
    }
    lastNode?.next = firstNode

    val navigationMap: MutableMap<String, Pair<String, String>> = mutableMapOf()
    mapInput.forEach {
        val origin = it.slice(0..2)
        val left = it.slice(7..9)
        val right = it.slice(12..14)
        navigationMap[origin] = Pair(left, right)
    }
    return Pair(firstNode!!, navigationMap)
}

fun move(start: Pair<String, DirectionNode>, navigationMap: MutableMap<String,Pair<String, String>>): Pair<String, DirectionNode> {
    val (left, right) = navigationMap[start.first]!!
    return Pair(if (start.second.direction == 'L') left else right, start.second.next!!)
}

fun findStart(start: String, firstNode: DirectionNode, navigationMap: MutableMap<String,Pair<String, String>>): Int {
    var steps = 0
    var fast = Pair(start, firstNode)
    var slow = Pair(start, firstNode)

    fast = move(move(fast, navigationMap), navigationMap)
    slow = move(slow, navigationMap)
    while(fast != slow) {
        fast = move(move(fast, navigationMap), navigationMap)
        slow = move(slow, navigationMap)
    }

    slow = Pair(start, firstNode)
    while(fast != slow) {
        fast = move(move(fast, navigationMap), navigationMap)
        slow = move(slow, navigationMap)
        steps += 1
    }

    // It seems a little _too_ serendipitous that the starting length is the same as the loop size, but I'll take it
    // var loopSize = 1
    // var zCount = 0
    // fast = move(move(fast, navigationMap), navigationMap)
    // slow = move(slow, navigationMap)
    // while(fast != slow) {
    //     fast = move(move(fast, navigationMap), navigationMap)
    //     slow = move(slow, navigationMap)
    //     loopSize += 1
    //     zCount += if (slow.first.last() == 'Z') 1 else 0
    // }

    return steps
}

fun gcd(a: Long, b: Long): Long {
    var aa = a
    var bb = b
    var t: Long
    while (bb != 0.toLong()) {
        t = bb
        bb = aa % bb
        aa = t
    }
    return aa
}

fun lcm(a: Long, b: Long): Long {
    return a * b / gcd(a, b)
}

fun lcm(numbers: List<Long>): Long {
    if (numbers.size == 1) {
        return numbers[0]
    } else if (numbers.size == 2) {
        return lcm(numbers[0], numbers[1])
    }
    return lcm(numbers[0], lcm(numbers.slice(1..numbers.size-1)))
}

fun main() {
    fun part1(input: List<String>): Int {
        val (firstNode, navigationMap) = loadInput(input)

        val start = "AAA"
        val end = "ZZZ"

        var current = firstNode
        var location = start
        var steps = 0
        while(location != end) {
            val (left, right) = navigationMap[location]!!
            location = if (current.direction == 'L') left else right
            current = current.next!!
            steps += 1
        }

        return steps
    }

    fun part2(input: List<String>): Long {
        val (firstNode, navigationMap) = loadInput(input)

        var (locations, _) = navigationMap.keys.partition { it.last() == 'A' }

        val loopSizes = locations.map { findStart(it, firstNode, navigationMap).toLong() }

        return lcm(loopSizes)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 2)
    //check(part2(testInput2) == 6)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
