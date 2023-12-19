package dev.passerby.technocom_test

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.passerby.technocom_test.databinding.FragmentFinishBinding

class FinishFragment : Fragment() {

    private var _binding: FragmentFinishBinding? = null
    private val binding: FragmentFinishBinding
        get() = _binding ?: throw RuntimeException("FragmentFinishBinding is null")

    private var coins = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preferences = requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)
        binding.apply {
            finishCoinsTextView.text = coins.toString()
            finishDoubleRewardButton.setOnClickListener {
                coins *= 2
                finishCoinsTextView.text = coins.toString()
                finishDoubleRewardButton.isEnabled = false
            }
            finishHomeButton.setOnClickListener {
                val saveCoins = preferences.getInt("coins", 100) + coins
                preferences.edit().putInt("coins", saveCoins).apply()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, StartFragment()).commit()
            }
        }
    }

    private fun parseParams() {
        val args = requireArguments()

        if (!args.containsKey(COINS)) {
            throw RuntimeException("Param coins is absent")
        }
        coins = args.getInt(COINS)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val COINS = "coins"

        fun newInstance(coins: Int): FinishFragment {
            return FinishFragment().apply {
                arguments = Bundle().apply {
                    putInt(COINS, coins)
                }
            }
        }
    }
}