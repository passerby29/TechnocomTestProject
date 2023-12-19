package dev.passerby.technocom_test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.passerby.technocom_test.data.BoardSize
import dev.passerby.technocom_test.data.MemoryCard
import kotlin.math.min

class GameBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener: OnCardClickListener
) :
    RecyclerView.Adapter<GameBoardAdapter.GameBoardViewHolder>() {

    companion object {
        private const val MARGIN_SIZE = 8
    }

    interface OnCardClickListener {
        fun cardClickListener(position: Int, imageView: ImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameBoardViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength = min(cardWidth, cardHeight)

        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams =
            view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return (GameBoardViewHolder(view))

    }

    override fun onBindViewHolder(holder: GameBoardViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = boardSize.numCards

    inner class GameBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val memoryCard = itemView.findViewById<ImageButton>(R.id.imageButton)
        fun bind(position: Int) {
            val card = cards[position]
            memoryCard.setImageResource(
                if (card.isFaceUp) {
                    cards[position].identifier
                } else {
                    R.drawable.item_card_placeholder
                }
            )
            memoryCard.alpha = if (card.isMatched) .6f else 1.0f
            if (card.isMatched) ContextCompat.getColorStateList(
                context,
                android.R.color.darker_gray
            )
            memoryCard.setOnClickListener {
                cardClickListener.cardClickListener(position, memoryCard)
            }
        }
    }
}