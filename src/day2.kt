import java.io.File
import kotlin.math.max
import kotlin.system.exitProcess

private const val DAY = 2

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = loadInput(DAY, false, transformer)

    println(input)

    val games = mutableListOf<GameRecord>()
    for (line in input) {
        line.split(":").let {
            val gameId = it[0].split(" ").last().toInt()
            val gameRecord = GameRecord(gameId)
            it[1].split(";").let {
                gameRecord.records = it.map {
                    val game = it.split(",").fold(Game()) { sum, el ->
                        el.split(" ").let {
                            when (it[2]) {
                                "green" -> sum.green = it[1].toInt()
                                "red" -> sum.red = it[1].toInt()
                                "blue" -> sum.blue = it[1].toInt()
                            }
                            sum
                        }
                    }
                    game
                }
            }
            games.add(gameRecord)
        }
    }
    solvePart1(games)
    solvePart2(games)
}

data class GameRecord(val id: Int, var records: List<Game> = mutableListOf())
data class Game(var red: Int = 0, var green: Int = 0, var blue: Int = 0)

// Part 1
private fun solvePart1(games: List<GameRecord>) {
    val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)
    val result = games.fold(0) { sum: Int, el: GameRecord ->
        if (el.records.all { game -> game.blue <= limits["blue"]!! && (game.red <= limits["red"]!!) && game.green <= limits["green"]!! })
            sum + el.id
        else sum
    }
    println(result)
}

// Part 2
private fun solvePart2(games: List<GameRecord>) {
    val result = games.map { el: GameRecord ->
        val mins = el.records.fold(mutableListOf(0, 0, 0)) { agg, el ->
            agg[0] = max(agg[0], el.red)
            agg[1] = max(agg[1], el.blue)
            agg[2] = max(agg[2], el.green)
            agg
        }
        mins.reduce(Int::times)
    }.sum()
    print(result)
}