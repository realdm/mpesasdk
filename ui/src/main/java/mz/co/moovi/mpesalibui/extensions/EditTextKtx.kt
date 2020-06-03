package mz.co.moovi.mpesalibui.extensions

import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText

fun AppCompatEditText.setTextWithoutListener(text: String, textWatcher: TextWatcher) {
    if (this.text.toString() != text) {
        this.removeTextChangedListener(textWatcher)
        this.setText(text)
        this.addTextChangedListener(textWatcher)
    }
}

fun createAfterTextChangeWatcher(listener: (Editable?) -> Unit): TextWatcher {
    return object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            listener.invoke(editable)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}