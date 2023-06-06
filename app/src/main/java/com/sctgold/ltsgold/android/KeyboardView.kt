package com.sctgold.ltsgold.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.sctgold.ltsgold.android.R
import kotlin.math.min

fun EditText.suppressSoftKeyboard() {
    showSoftInputOnFocus = false
}

internal abstract class ViewRunnable : Runnable {
    var view: View? = null
}

internal class KeyClickListener(
    private val field: EditText?,
    private val maxLength: Int
) : View.OnClickListener {
    override fun onClick(view: View?) {
        val field = this.field ?: return

        if (view is IconifiedTextView) {
            field.removeCharBeforeSelection()
        } else if (view is TextView) {
            val newChar = view.text[0]
            field.addCharAfterSelection(newChar)
        }
    }

    private fun EditText.addCharAfterSelection(newChar: Char) {
        if (isBiggerThanMaxLength(text)) {
            return
        }

        val selectionEnd = this.selectionEnd
        val newTextBuilder = StringBuilder()
            .append(text.subSequence(0, selectionEnd))
            .append(newChar)
            .append(text.subSequence(selectionEnd, length()))

        setText(newTextBuilder)
        setSelectionWithValidation(selectionEnd + 1) // set selection to the next character
    }

    private fun isBiggerThanMaxLength(
        text: CharSequence
    ) = maxLength > 0 && text.length >= maxLength

    private fun EditText.removeCharBeforeSelection() {
        val removableCharPosition = selectionEnd - 1
        if (removableCharPosition < 0) {
            return // do nothing if there are no characters before the cursor
        }

        val newTextBuilder = StringBuilder()
            .append(text.substring(0, removableCharPosition))
            .append(text.substring(removableCharPosition + 1))

        setText(newTextBuilder)
        setSelectionWithValidation(removableCharPosition)
    }

    private fun EditText.setSelectionWithValidation(index: Int) {
        setSelection(min(index, text.length))
    }
}

class KeyboardView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(context, attrs, -1)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttributes(context, attrs, defStyleAttr)
    }

    private val removeChar: ViewRunnable = object : ViewRunnable() {
        override fun run() {
            val listener = KeyClickListener(field, fieldMaxLength)

            listener.onClick(view)
            handler.postDelayed(this, 50)
        }
    }

    private var fieldId: Int = 0

    var field: EditText? = null
        set(value) {
            value?.suppressSoftKeyboard()
            field = value
            if (childCount > 0) {
                updateView(getChildAt(0))
            }
        }

    var fieldMaxLength: Int = 0
        set(value) {
            field = value
            if (childCount > 0) {
                updateView(getChildAt(0))
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    var keyHeight: Int = 0
        set(value) {
            field = value
            if (childCount > 0) {
                updateView(getChildAt(0))
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    var keyTextSize: Float = 0F
        set(value) {
            field = value
            if (childCount > 0) {
                updateView(getChildAt(0))
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    var keyTextColor: Int = 0
        set(value) {
            field = value
            if (childCount > 0) {
                updateView(getChildAt(0))
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    var keySpecialValue: String = ""
        set(value) {
            field = value
            if (childCount > 0) {
                updateView(getChildAt(0))
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    var keySpecialListener: OnClickListener? = null
        set(value) {
            field = value
            if (childCount > 0) {
                updateView(getChildAt(0))
            }
        }

    private fun initAttributes(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        val attributes = context.theme.obtainStyledAttributes(attrs,
            R.styleable.NumericKeyboard, defStyleAttr, 0)
        val defaultKeyTextSize = context.resources.getDimensionPixelSize(R.dimen.keyboard_text_size)

        fieldId = attributes.getResourceId(R.styleable.NumericKeyboard_field, 0)
        fieldMaxLength = attributes.getInteger(R.styleable.NumericKeyboard_fieldMaxLength, 0)

        keyTextSize = attributes.getDimensionPixelSize(R.styleable.NumericKeyboard_keyTextSize, defaultKeyTextSize).toFloat()
        keyTextColor = attributes.getColor(R.styleable.NumericKeyboard_keyTextColor, Color.BLACK)

        keySpecialValue = attributes.getString(R.styleable.NumericKeyboard_keySpecial) ?: ""

        post { initViews() }
    }

    private fun initViews() {
        if (field == null) {
            field = rootView.findViewById(fieldId)
        }

        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.keyboard, this, false)

        updateView(layout)
        addView(layout)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateView(layout: View) {
        val key1 = layout.findViewById<TextView>(R.id.key1)
        val key2 = layout.findViewById<TextView>(R.id.key2)
        val key3 = layout.findViewById<TextView>(R.id.key3)
        val key4 = layout.findViewById<TextView>(R.id.key4)
        val key5 = layout.findViewById<TextView>(R.id.key5)
        val key6 = layout.findViewById<TextView>(R.id.key6)
        val key7 = layout.findViewById<TextView>(R.id.key7)
        val key8 = layout.findViewById<TextView>(R.id.key8)
        val key9 = layout.findViewById<TextView>(R.id.key9)
        val key0 = layout.findViewById<TextView>(R.id.key0)
        val keyRemove = layout.findViewById<TextView>(R.id.keyRemove)
        val keySpecial = layout.findViewById<TextView>(R.id.keySpecial)
        val listener = KeyClickListener(field, fieldMaxLength)

        key1.setTextColor(keyTextColor)
        key2.setTextColor(keyTextColor)
        key3.setTextColor(keyTextColor)
        key4.setTextColor(keyTextColor)
        key5.setTextColor(keyTextColor)
        key6.setTextColor(keyTextColor)
        key7.setTextColor(keyTextColor)
        key8.setTextColor(keyTextColor)
        key9.setTextColor(keyTextColor)
        key0.setTextColor(keyTextColor)
        keyRemove.setTextColor(keyTextColor)
        keySpecial.setTextColor(keyTextColor)

        key1.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key2.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key3.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key4.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key5.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key6.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key7.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key8.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key9.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        key0.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)
        keyRemove.setTextSize(TypedValue.COMPLEX_UNIT_PX, 0.8F * keyTextSize)
        keySpecial.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTextSize)

        key1.setOnClickListener(listener)
        key2.setOnClickListener(listener)
        key3.setOnClickListener(listener)
        key4.setOnClickListener(listener)
        key5.setOnClickListener(listener)
        key6.setOnClickListener(listener)
        key7.setOnClickListener(listener)
        key8.setOnClickListener(listener)
        key9.setOnClickListener(listener)
        key0.setOnClickListener(listener)

        keyRemove.setOnClickListener(listener)
        keyRemove.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> handler.postDelayed(removeChar.also { it.view = view }, 750)
                MotionEvent.ACTION_UP -> handler.removeCallbacks(removeChar)
            }

            false
        }

        keySpecial.text = keySpecialValue
        keySpecial.isEnabled = keySpecialValue.isNotEmpty()
        keySpecial.setOnClickListener(keySpecialListener ?: listener)
    }
}

@SuppressLint("AppCompatCustomView")
internal class IconifiedTextView : TextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        setIconifiedTypeface()
    }

    private fun setIconifiedTypeface() {
        typeface = ResourcesCompat.getFont(context, R.font.material_icons)
    }
}