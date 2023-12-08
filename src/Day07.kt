val tieBreakerMap = mapOf(
    'A' to 0, 
    'K' to 1, 
    'Q' to 2, 
    'J' to 3, 
    'T' to 4, 
    '9' to 5, // What a way to make a living 
    '8' to 6, 
    '7' to 7, 
    '6' to 8, 
    '5' to 9, 
    '4' to 10, 
    '3' to 11,
    '2' to 12
)

val tieBreakerMap2 = mapOf(
    'A' to 0, 
    'K' to 1, 
    'Q' to 2, 
    'J' to 13, 
    'T' to 4, 
    '9' to 5, // What a way to make a living 
    '8' to 6, 
    '7' to 7, 
    '6' to 8, 
    '5' to 9, 
    '4' to 10, 
    '3' to 11,
    '2' to 12
)

val FIVE_OF_A_KIND = 0
val FOUR_OF_A_KIND = 1
val FULL_HOUSE = 2
val THREE_OF_A_KIND = 3
val TWO_PAIR = 4
val ONE_PAIR = 5
val HIGH_CARD = 6

fun scoreHand(hand: String): Int {
    var score = 0
    val cardCountsMap = mutableMapOf<Char, Int>().withDefault({_ -> 0})
    hand.forEach {
        char -> 
        score = score shl 4
        score += tieBreakerMap[char]!!
        cardCountsMap[char] = cardCountsMap.getValue(char) + 1
    }

    val cardCounts = cardCountsMap.values.sortedDescending()
    var baseScore = HIGH_CARD
    if (cardCounts[0] == 5) {
        baseScore = FIVE_OF_A_KIND
    } else if (cardCounts[0] == 4) {
        baseScore = FOUR_OF_A_KIND
    } else if (cardCounts[0] == 3 && cardCounts[1] == 2) {
        baseScore = FULL_HOUSE
    } else if (cardCounts[0] == 3) {
        baseScore = THREE_OF_A_KIND
    } else if (cardCounts[0] == 2 && cardCounts[1] == 2) {
        baseScore = TWO_PAIR
    } else if (cardCounts[0] == 2) {
        baseScore = ONE_PAIR
    }

    return score + (baseScore shl 20)
}

fun scoreHand2(hand: String): Int {
    var score = 0
    val cardCountsMap = mutableMapOf<Char, Int>().withDefault({_ -> 0})
    hand.forEach {
        char -> 
        score = score shl 4
        score += tieBreakerMap2[char]!!
        cardCountsMap[char] = cardCountsMap.getValue(char) + 1
    }

    val jokerCount = cardCountsMap.getValue('J')
    cardCountsMap.remove('J')
    val cardCounts = cardCountsMap.values.sortedDescending()
    var baseScore = HIGH_CARD
    if (jokerCount == 5 || cardCounts[0] + jokerCount == 5) {
        baseScore = FIVE_OF_A_KIND
    } else if (cardCounts[0] + jokerCount == 4) {
        baseScore = FOUR_OF_A_KIND
    } else if (cardCounts[0] + jokerCount == 3 && cardCounts[1] == 2) {
        baseScore = FULL_HOUSE
    } else if (cardCounts[0] + jokerCount == 3) {
        baseScore = THREE_OF_A_KIND
    } else if (cardCounts[0] + jokerCount == 2 && cardCounts[1] == 2) {
        baseScore = TWO_PAIR
    } else if (cardCounts[0] + jokerCount == 2) {
        baseScore = ONE_PAIR
    }

    return score + (baseScore shl 20)
}

fun main() {
    fun part1(input: List<String>): Int {
        val bidMap: MutableMap<Int,Int> = mutableMapOf()

        input.forEach {
            val (hand, bidString) = it.split(" ")
            val score = scoreHand(hand)
            val bid = bidString.toInt()
            bidMap[score] = bid
        }

        var total = 0
        bidMap.keys.sortedDescending().forEachIndexed {
            i, score ->
            total += (i+1) * bidMap[score]!!
        }
        return total
    }

    fun part2(input: List<String>): Int {
        val bidMap: MutableMap<Int,Int> = mutableMapOf()

        input.forEach {
            val (hand, bidString) = it.split(" ")
            val score = scoreHand2(hand)
            val bid = bidString.toInt()
            bidMap[score] = bid
        }

        var total = 0
        bidMap.keys.sortedDescending().forEachIndexed {
            i, score ->
            total += (i+1) * bidMap[score]!!
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
