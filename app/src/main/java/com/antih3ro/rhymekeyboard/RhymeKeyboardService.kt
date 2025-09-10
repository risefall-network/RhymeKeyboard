@file:Suppress("DEPRECATION")

package com.antih3ro.rhymekeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import com.antih3ro.rhymekeyboard.databinding.KeyboardLayoutBinding
import android.util.Log // Import Log for debugging

@Suppress("DEPRECATION") // Keep for now as Keyboard and KeyboardView are deprecated but widely used
class RhymeKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var binding: KeyboardLayoutBinding
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var statusText: TextView
    private lateinit var aggregateToggle: CheckBox
    private lateinit var keyboardView: KeyboardView

    private val rhymeClient = RhymeClient()
    private val serviceScope = MainScope()

    private var isShiftActive = false

    companion object {
        private const val TAG = "RhymeKeyboardService" // Define a TAG for logging
        private const val KEYCODE_ENTER_CUSTOM = 10
        private const val KEYCODE_SYMBOLS_CUSTOM = -2
        private const val KEYCODE_RHYME_BUTTON = -7
        private const val MAX_CONTEXT_LENGTH = 2000
        private const val MAX_RHYMES_PER_WORD = 12
        private const val MAX_SUGGESTIONS_DISPLAYED = 24
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView: Started")

        // Inflate the layout using View Binding
        binding = KeyboardLayoutBinding.inflate(LayoutInflater.from(this))
        val root = binding.root
        Log.d(TAG, "onCreateInputView: Layout inflated via View Binding")

        // Initialize views using binding
        keyboardView = binding.keyboardView
        statusText = binding.statusText
        aggregateToggle = binding.aggregateToggle
        Log.d(TAG, "onCreateInputView: Views initialized from binding")

        // Setup RecyclerView
        suggestionAdapter = SuggestionAdapter { word ->
            currentInputConnection?.commitText(word, 1)
        }
        // LinearLayoutManager is added by app:layoutManager in XML, no need to set here
        // binding.suggestionRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.suggestionRecyclerView.adapter = suggestionAdapter
        Log.d(TAG, "onCreateInputView: RecyclerView setup complete")

        // Load the keyboard definition XML
        val keyboard = Keyboard(this, R.xml.qwerty)
        Log.d(TAG, "onCreateInputView: Keyboard object created from R.xml.qwerty")

        keyboardView.keyboard = keyboard // Set the keyboard to the KeyboardView
        keyboardView.setOnKeyboardActionListener(this) // Set this service as the listener
        Log.d(TAG, "onCreateInputView: Keyboard and listener set on KeyboardView")

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onStartInput(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "onStartInput: Input started, restarting = $restarting")
        setSuggestions(emptyList())
        statusText.text = getString(R.string.status_ready)
        isShiftActive = false
        // Update shift state on the keyboard view if it supports it visually
        keyboardView.isShifted = isShiftActive
        keyboardView.invalidateAllKeys()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "onDestroy: Service destroyed and scope cancelled")
    }

    // --- KeyboardView.OnKeyboardActionListener methods ---
    override fun onPress(primaryCode: Int) {
        Log.d(TAG, "onPress: code = $primaryCode")
    }

    override fun onRelease(primaryCode: Int) {
        Log.d(TAG, "onRelease: code = $primaryCode")
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        Log.d(TAG, "onKey: primaryCode = $primaryCode, keyCodes = ${keyCodes?.joinToString()}")
        val ic = currentInputConnection ?: return

        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                ic.deleteSurroundingText(1, 0)
            }
            Keyboard.KEYCODE_SHIFT -> {
                isShiftActive = !isShiftActive
                keyboardView.isShifted = isShiftActive // Update KeyboardView's internal shift state
                keyboardView.invalidateAllKeys() // Redraw all keys to reflect shift state
            }
            KEYCODE_ENTER_CUSTOM -> {
                ic.commitText(
                    ""
                    , 1)
                            requestSuggestions ()
            }
            KEYCODE_SYMBOLS_CUSTOM -> {
                // TODO: Implement logic to switch to a symbols keyboard if desired
                Log.d(TAG, "onKey: Symbols key pressed, not implemented yet.")
            }
            KeyEvent.KEYCODE_SPACE -> {
                ic.commitText(" ", 1)
                requestSuggestions()
            }
            KEYCODE_RHYME_BUTTON -> {
                requestSuggestions()
            }
            else -> {
                val ch = primaryCode.toChar()
                val toCommit = if (isShiftActive) ch.uppercaseChar().toString() else ch.toString()
                ic.commitText(toCommit, 1)
            }
        }
    }

    override fun onText(text: CharSequence?) {
        Log.d(TAG, "onText: $text")
        text?.let {
            currentInputConnection?.commitText(it, 1)
        }
    }

    override fun swipeLeft() { Log.d(TAG, "swipeLeft") }
    override fun swipeRight() { Log.d(TAG, "swipeRight") }
    override fun swipeDown() { Log.d(TAG, "swipeDown") }
    override fun swipeUp() { Log.d(TAG, "swipeUp") }
    // --- End of KeyboardView.OnKeyboardActionListener methods ---

    private var fetchJob: Job? = null

    @SuppressLint("ResourceType")
    private fun requestSuggestions() {
        Log.d(TAG, "requestSuggestions: Started")
        val ic = currentInputConnection ?: run {
            Log.w(TAG, "requestSuggestions: CurrentInputConnection is null, cannot request suggestions.")
            return
        }
        val before = ic.getTextBeforeCursor(MAX_CONTEXT_LENGTH, 0) ?: ""

        val aggregate = aggregateToggle.isChecked
        statusText.text = if (aggregate) getString(R.string.status_aggregating) else getString(R.string.status_listening)
        Log.d(TAG, "requestSuggestions: Aggregate = $aggregate, Text before cursor = '$before'")

        fetchJob?.cancel()
        fetchJob = serviceScope.launch {
            val targets: List<String> = if (aggregate) {
                Utils.allLineEndWords(before)
            } else {
                listOfNotNull(Utils.lastWordOfPreviousLine(before))
            }
            Log.d(TAG, "requestSuggestions: Targets for rhymes = $targets")

            if (targets.isEmpty()) {
                setSuggestions(emptyList())
                statusText.text = getString(R.string.status_no_rhymes)
                Log.d(TAG, "requestSuggestions: No targets, setting no rhymes status.")
                return@launch
            }

            val allRhymes = mutableSetOf<String>()
            try {
                withContext(Dispatchers.IO) {
                    for (w in targets) {
                        Log.d(TAG, "requestSuggestions: Fetching rhymes for '$w'")
                        val rhymes = rhymeClient.fetchRhymes(w, max = MAX_RHYMES_PER_WORD)
                        allRhymes.addAll(rhymes)
                    }
                }
                setSuggestions(allRhymes.take(MAX_SUGGESTIONS_DISPLAYED))
                val rhymeCount = allRhymes.size
                statusText.text = if (rhymeCount == 0) {
                    getString(R.string.status_no_rhymes)
                } else {
                    resources.run { resources.getQuantityString(R.plurals.status_rhyme_count, rhymeCount, rhymeCount) }
                }
                Log.d(TAG, "requestSuggestions: Found $rhymeCount rhymes. Displaying ${allRhymes.take(MAX_SUGGESTIONS_DISPLAYED).size}")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                statusText.text = getString(R.string.status_error_fetching)
                Log.e(TAG, "Error fetching rhymes", e) // Uncommented Log.e
            }
        }
    }

    private fun setSuggestions(words: List<String>) {
        Log.d(TAG, "setSuggestions: Updating suggestions with ${words.size} words.")
        suggestionAdapter.submitList(words)
    }
}