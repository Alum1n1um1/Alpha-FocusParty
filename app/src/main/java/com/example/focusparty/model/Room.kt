package com.example.focusparty.model

data class Room(
    val id:String="",
    val name:String="",
    val owner:String="",
    val description:String="",
    val status: Int=0,
    val members: List<String>,
    val jalons: List<Jalon>,
    val timer: Timer = Timer(),
    val tempsTotal: Long = 0L,
    val points: Int = 0,
    val jalonsTermines: Int = 0,
    val level : Int = 0,
    val exp : Int = 0
)


fun Room.computeLevel(totalMembersExp: Long): Int {
    var totalExp = totalMembersExp + (jalonsTermines * 250L)

    var level = 1
    var remaining = totalExp

    while (true) {
        val required = 50L * level
        if (remaining >= required) {
            remaining -= required
            level++
        } else {
            return level
        }
    }
}
