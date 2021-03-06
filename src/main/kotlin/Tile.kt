typealias Color = Char
typealias Pos = Pair<Int, Int>

class Tile(_squares: Map<Pos, Color> = mapOf(), pad: Boolean = false) : Comparable<Tile> {
    override fun compareTo(other: Tile): Int {
        // look away
        val compare = hashCode().compareTo(other.hashCode())
        return if (compare == 0 && this != other) {
            this.squares.hashCode().compareTo(other.hashCode())
        } else {
            compare
        }
    }

    companion object {
        @JvmField val blank = ' '
    }
    data class SerializableTile(
        val squares: Map<Pos, Color>,
        val size: Int,
        val width: Int,
        val height: Int,
        val dims: Pos
    ) {
        companion object {
            fun fromTile(tile: Tile): SerializableTile = SerializableTile(
                squares = tile.squares,
                size = tile.size,
                width = tile.width,
                height = tile.height,
                dims = tile.dims
            )
        }
    }

    private val squares = normalize(_squares.filterValues { it != blank }, pad)
    val size = squares.size
    val width = squares.keys.map { it.first } .max() ?.plus(1) ?: 0
    val height = squares.keys.map { it.second } .max() ?.plus(1) ?: 0
    val dims = width to height

    override fun equals(other: Any?): Boolean = other is Tile && squares == other.squares
    override fun hashCode(): Int = squares.hashCode()

    operator fun get(x: Int, y: Int): Color? = squares[x to y]

    fun without(tile: Tile, x: Int, y: Int): Tile = Tile(
        squares.filter { (pos, _) -> (pos.first - x to pos.second - y) !in tile.squares },
        pad = true
    )

    fun fitAt(board: Tile, x: Int, y: Int): Boolean = squares.all { (pos, color) ->
        color == board[x + pos.first, y + pos.second]
    }

    val reflectLR: Tile by lazy {
        Tile(squares.mapKeys { (pos, _) -> width - pos.first - 1 to pos.second })
    }

    val reflectUD: Tile by lazy {
        Tile(squares.mapKeys { (pos, _) -> pos.first to height - pos.second - 1 })
    }

    val reflectPrim: Tile by lazy {
        Tile(squares.mapKeys { (pos, _) -> pos.second to pos.first })
    }

    val reflectOff: Tile by lazy { this.rotateAbout.reflectPrim }

    val rotateAbout: Tile by lazy { this.reflectLR.reflectUD }

    val rotateLeft: Tile by lazy { this.reflectPrim.reflectUD }

    val rotateRight: Tile by lazy { this.reflectPrim.reflectLR }

    val rotations: List<Tile> by lazy {
        listOf(this, rotateAbout, rotateLeft, rotateRight).distinct()
    }

    val transformations: List<Tile> by lazy {
        listOf(this, reflectLR, reflectUD, reflectPrim, reflectOff, rotateAbout, rotateLeft, rotateRight)
            .distinct()
    }

    val reflections: List<Tile> by lazy {
        listOf(this, reflectLR, reflectUD, reflectPrim, reflectOff).distinct()
    }

    val noTransform: List<Tile> by lazy { listOf(this) }

    fun copy(): Tile {
        return Tile(squares)
    }

    override fun toString(): String = (0 until height).joinToString(separator = "\n") { r ->
        (0 until width).map { c -> this[c, r] ?: blank }.joinToString(separator = "")
    }
}

fun normalize(rawSquares: Map<Pos, Color>, pad: Boolean = false): Map<Pos, Color> {
    val squares = rawSquares.filterValues { it != Tile.blank }
    if (pad) {
        return squares
    }
    val minRow = squares.keys.map { it.first } .min() ?: 0
    val minCol = squares.keys.map { it.second } .min() ?: 0
    return squares.mapKeys { (pos, _) -> pos.first - minRow to pos.second - minCol }
}
