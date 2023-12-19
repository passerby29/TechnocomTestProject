package dev.passerby.technocom_test

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dev.passerby.technocom_test.data.BoardSize
import dev.passerby.technocom_test.data.MemoryGame
import dev.passerby.technocom_test.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")


    private lateinit var memoryGame: MemoryGame
    private lateinit var gameBoardAdapter: GameBoardAdapter
    private var boardSize = BoardSize.STANDARD

    private lateinit var context: Context

    private var moves = 0
    private var coins = 100

    private var timerSeconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            timerSeconds++

            if (timerSeconds > 20) {
                if (coins != 10) {
                    coins -= 5
                    binding.gameCoinsTextView.text = coins.toString()
                }
            }

            val minutes = (timerSeconds % 3600) / 60
            val seconds = timerSeconds % 60

            val time = String.format("%02d:%02d", minutes, seconds)
            binding.gameTimerTextView.text = time
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = view.context
        memoryGame = MemoryGame(boardSize)
        gameBoardAdapter = GameBoardAdapter(
            context,
            boardSize,
            memoryGame.cards,
            object : GameBoardAdapter.OnCardClickListener {
                override fun cardClickListener(position: Int, imageView: ImageView) {
                    flipTextView(imageView, position).start()
                }

            })
        binding.apply {
            gameBoardRecyclerView.apply {
                adapter = gameBoardAdapter
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, boardSize.getWidth())
            }
        }

        handler.postDelayed(runnable, 1000)
    }

    private fun updateGameWithFlip(position: Int) {
        if (memoryGame.isCardFacedUp(position)) {
            Snackbar.make(binding.gameMainContainer, "Invalid move!", Snackbar.LENGTH_SHORT)
                .show()
            return
        }
        if (memoryGame.flipCard(position)) {
            if (memoryGame.haveWonGame()) {
                openFinishFragment()
            }
        }
        moves = memoryGame.getNumMoves()
    }

    private fun openFinishFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, FinishFragment.newInstance(coins))
            .commit()
    }

    private fun flipTextView(
        textView: ImageView,
        position: Int,
        mDuration: Long = 300L,
    ): AnimatorSet {

        val flip90degrees = ObjectAnimator.ofFloat(textView, "rotationX", 0f, 90f).apply {
            duration = mDuration
            doOnEnd {
                updateGameWithFlip(position)
            }
        }
        val flip90degreesBack = ObjectAnimator.ofFloat(textView, "rotationX", 90f, 0f).apply {
            duration = mDuration
            doOnEnd {
                gameBoardAdapter.notifyDataSetChanged()
            }
        }

        return AnimatorSet().apply {
            interpolator = AccelerateDecelerateInterpolator()

            play(flip90degrees).before(flip90degreesBack)
            play(flip90degreesBack)
        }
    }
}