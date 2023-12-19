package dev.passerby.technocom_test.data

enum class BoardSize(val numCards: Int) {
    STANDART(20);

    fun getWidth(): Int {
        return when (this) {
            STANDART -> 4
        }
    }

    fun getHeight() = numCards / 4

    fun getNumPairs() = numCards / 2
}