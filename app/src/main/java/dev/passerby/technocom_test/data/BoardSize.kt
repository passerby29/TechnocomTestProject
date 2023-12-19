package dev.passerby.technocom_test.data

enum class BoardSize(val numCards: Int) {
    STANDARD(20);

    fun getWidth(): Int {
        return when (this) {
            STANDARD -> 4
        }
    }

    fun getHeight() = numCards / 4

    fun getNumPairs() = numCards / 2
}