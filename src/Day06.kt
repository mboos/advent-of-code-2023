import kotlin.math.sqrt

fun main() {
    fun part1(input: List<String>): Int {
        // total time T
        // hold time t
        // distance = d, min distance = D
        // t^2 - Tt + d >= 0 => lose race
        // t^2 - Tt + d < 0 => win 
        // quadratic formula x = (-b +/- sqrt(b^2 - 4*a*c))/(2a)
        //                   t = (T +/- sqrt(T^2 - 4d))/2
        // if we want d > D, (T - sqrt(T^2 - 4D))/2 < t < (T + sqrt(T^2 - 4D))/2

        val numberPattern = "\\d+".toRegex()
        val times = numberPattern.findAll(input[0]).map { it.value.toDouble() }
        val distances = numberPattern.findAll(input[1]).map { it.value.toDouble() }
        var product = 1

        times.zip(distances).forEach {
            (t, d): Pair<Double, Double> ->
            val squared = sqrt(t*t - 4 * d)
            var lower = ((t - squared)/2).toInt()
            var upper = ((t + squared)/2).toInt()

            if (lower * (t - lower) <= d) {
                lower += 1
            }
            if (upper * (t -upper) <= d) {
                upper -= 1
            }
            product *= (upper-lower+1)
        }

        return product
    }

    fun part2(input: List<String>): Int {
        val numberPattern = "\\d+".toRegex()
        val t = numberPattern.find(input[0].replace(" ", ""))!!.value.toDouble() 
        val d = numberPattern.find(input[1].replace(" ", ""))!!.value.toDouble() 

        val squared = sqrt(t*t - 4 * d)
        var lower = ((t - squared)/2).toInt()
        var upper = ((t + squared)/2).toInt()

        if (lower * (t - lower) <= d) {
            lower += 1
        }
        if (upper * (t -upper) <= d) {
            upper -= 1
        }
        return (upper-lower+1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
