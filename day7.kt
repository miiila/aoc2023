import java.io.File
import kotlin.system.exitProcess

private const val DAY = 7

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x.split(" ").let { Pair(it.first(), it.last().toInt()) } }
    val input = loadInput(DAY, false, transformer)

    println(input)
    println(solvePart1(input))
    println(solvePart2(input))
}

// Part 1
private fun solvePart1(input: List<Pair<String, Int>>): Int {
    val cards = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2').reversed()
    val comp =
        Comparator { c1: Pair<String, Int>, c2: Pair<String, Int> -> compareSameCards(c1.first, c2.first, cards) }
    val sorted =
        input.sortedWith(comp).sortedBy { getCardStrength(it.first) }
    return sorted.mapIndexed { i, card -> (i + 1) * card.second }.sum()
}

fun compareSameCards(card1: String, card2: String, cards: List<Char>): Int {
    var i = -1
    while (true) {
        i++
        if (card1[i] == card2[i]) {
            continue
        }
        return cards.indexOf(card1[i]) - cards.indexOf(card2[i])
    }
}

fun getCardStrength(card: String): Int {
    val ranked = card.groupingBy { it }.eachCount().values

    return when {
        5 in ranked -> 50
        4 in ranked -> 40
        3 in ranked && 2 in ranked -> 30
        3 in ranked -> 20
        ranked.groupingBy { it }.eachCount()[2] == 2 -> 10
        2 in ranked -> 5
        else -> 1
    }
}

// Part 2
private fun solvePart2(input: List<Pair<String, Int>>): Int {
    val cards = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J').reversed()
    val comp =
        Comparator { c1: Pair<String, Int>, c2: Pair<String, Int> -> compareSameCards(c1.first, c2.first, cards) }
    val sorted =
        input.sortedWith(comp).sortedBy { getCardStrengthWithJokers(it.first) }
    return sorted.mapIndexed { i, card -> (i + 1) * card.second }.sum()
}

fun getCardStrengthWithJokers(card: String): Int {
    val ranked_ = card.groupingBy { it }.eachCount().toMutableMap()
    if ('J' in ranked_) {
        val j = ranked_.remove('J')
        if (j == 5) {
            ranked_['A'] = 5
        } else {
            val rev = ranked_.entries.associate { (k, v) -> v to k }.toSortedMap().reversed()
            val u: Char = rev.firstEntry().value
            ranked_[u] = ranked_[u]!! + j!!
        }
    }
    val ranked = ranked_.values

    return when {
        5 in ranked -> 50
        4 in ranked -> 40
        3 in ranked && 2 in ranked -> 30
        3 in ranked -> 20
        ranked.groupingBy { it }.eachCount()[2] == 2 -> 10
        2 in ranked -> 5
        else -> 1
    }
}
