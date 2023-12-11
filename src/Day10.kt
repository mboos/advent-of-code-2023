
import 	java.util.PriorityQueue

data class Step(val distance:Int, val row:Int, val column:Int): Comparable<Step> {
    override fun compareTo(other:Step): Int {
        return distance.compareTo(other.distance)
    }
}

fun tryStep(queue: PriorityQueue<Step>, step: Step, allowed: String, input: List<String>) {
    if (step.row < 0 || step.row >= input.size || step.column < 0 || step.column >= input[0].length) {
        return
    }
    if (!allowed.contains(input[step.row][step.column])) {
        return
    }
    queue.add(step)
}

fun getLoop(input: List<String>): Pair<Set<Pair<Int, Int>>, Int> {
    val startRow = input.indexOfFirst { it.contains("S") }
        val startCol = input[startRow].indexOf('S')

        val visited: MutableSet<Pair<Int, Int>> = mutableSetOf()
        val queue: PriorityQueue<Step> = PriorityQueue()
        queue.add(Step(0, startRow, startCol))
        var maxDist = 0

        while (queue.peek() != null) {
            val step = queue.poll()
            val d = step.distance
            val r = step.row
            val c = step.column
            if (visited.contains(Pair(r, c))) {
                continue
            }
            if (r < 0 || c < 0 || r >= input.size || c >= input[0].length) {
                continue
            }
            maxDist = d
            visited.add(Pair(r, c))

            if (input[r][c] == 'S') {
                tryStep(queue, Step(d+1, r-1, c), "F7|", input)
                tryStep(queue, Step(d+1, r, c-1), "FL-", input)
                tryStep(queue, Step(d+1, r+1, c), "JL|", input)
                tryStep(queue, Step(d+1, r, c+1), "J7-", input)
            } else if (input[r][c] == '|') {
                tryStep(queue, Step(d+1, r-1, c), "F7|", input)
                tryStep(queue, Step(d+1, r+1, c), "JL|", input)
            } else if (input[r][c] == '-') {
                tryStep(queue, Step(d+1, r, c-1), "FL-", input)
                tryStep(queue, Step(d+1, r, c+1), "J7-", input)
            } else if (input[r][c] == 'J') {
                tryStep(queue, Step(d+1, r-1, c), "F7|", input)
                tryStep(queue, Step(d+1, r, c-1), "FL-", input)
            } else if (input[r][c] == 'L') {
                tryStep(queue, Step(d+1, r-1, c), "F7|", input)
                tryStep(queue, Step(d+1, r, c+1), "J7-", input)
            } else if (input[r][c] == '7') {
                tryStep(queue, Step(d+1, r, c-1), "FL-", input)
                tryStep(queue, Step(d+1, r+1, c), "JL|", input)
            } else if (input[r][c] == 'F') {
                tryStep(queue, Step(d+1, r, c+1), "J7-", input)
                tryStep(queue, Step(d+1, r+1, c), "JL|", input)
            } 
        }
        return Pair(visited, maxDist)
}

fun main() {
    fun part1(input: List<String>): Int {
        val (_, maxDist) = getLoop(input)
        return maxDist
    }

    fun part2(input: List<String>): Int {
        val (visited, _) = getLoop(input)
        val numRows = input.size
        val numColumns = input[0].length
        val enclosed: MutableSet<Pair<Int, Int>> = mutableSetOf()

        for(r in 0..numRows-1) {
            var crossings = 0
            var vertical = 0
            for (c in 0..numColumns-1) {
                if (visited.contains(Pair(r,c))) {
                    if (input[r][c] == '|') {
                        crossings += 1
                    } else if (input[r][c] == 'S') {
                        var above = false
                        var below = false
                        if (r > 0 && "F7|".contains(input[r-1][c])) {
                            above = true
                        }
                        if (r < input.size -1 && "JL|".contains(input[r+1][c])) {
                            below = true
                        }
                        if (above && below) {
                            crossings += 1
                        } else if (above) {
                            if (vertical > 0) {
                                crossings += 1
                                vertical = 0
                            } else if (vertical < 0) {
                                vertical = 0 
                            } else { 
                                vertical = -1
                            }
                        } else if (below) {
                            if (vertical < 0) {
                                crossings += 1
                                vertical = 0
                            } else if (vertical > 0) {
                                vertical = 0 
                            } else {
                                vertical = 1
                            }
                        }
                    } else if ("F7".contains(input[r][c])) {
                        if (vertical < 0) {
                            crossings += 1
                            vertical = 0
                        } else if (vertical > 0) {
                            vertical = 0 
                        } else {
                            vertical = 1
                        }
                    } else if ("JL".contains(input[r][c])) {
                        if (vertical > 0) {
                            crossings += 1
                            vertical = 0
                        } else if (vertical < 0) {
                            vertical = 0 
                        } else { 
                            vertical = -1
                        }
                    }
                } else if (crossings % 2 == 1) {
                    vertical = 0 
                    enclosed.add(Pair(r, c))
                } else {
                    vertical = 0
                }                
            }
        }
        return enclosed.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 8)
    val testInput2 = readInput("Day10_testb")
    check(part2(testInput2) == 8)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
