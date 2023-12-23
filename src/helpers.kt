import java.io.File
import java.net.URI

fun <T> loadInput(day: Int, test: Boolean = true, transformer: (input: String) -> T): List<T> {
    var path = "./day${day}_input"
    if (test) {
        path += "_test"
    }
    return File(path).readLines().map(transformer)
}

fun downloadInput(day: Int) {
    val cookie = System.getenv("AOC_COOKIE")
    URI("https://adventofcode.com/2023/day/${day}/input").toURL().openConnection().apply {
        setRequestProperty(
            "Cookie",
            "session=$cookie"
        )
    }.getInputStream().use { File("./day${day}_input").writeBytes(it.readAllBytes()) }
}

data class Pos(val row: Int, var col: Int)

fun getNext(pos: Pos): List<Pos> {
    return listOf(
        Pos(pos.row, pos.col + 1),
        Pos(pos.row + 1, pos.col),
        Pos(pos.row - 1, pos.col),
        Pos(pos.row, pos.col - 1),
    )
}

fun getNextWithDir(pos: Pos): List<Pair<Pos, Char>> {
    return listOf(
        Pair(Pos(pos.row, pos.col + 1), '>'),
        Pair(Pos(pos.row + 1, pos.col), 'v'),
        Pair(Pos(pos.row - 1, pos.col), '^'),
        Pair(Pos(pos.row, pos.col - 1), '<'),
    )
}
