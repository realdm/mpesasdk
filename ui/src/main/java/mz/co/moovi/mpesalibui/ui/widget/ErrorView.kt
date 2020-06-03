package mz.co.moovi.mpesalibui.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.mpesa_sdk_view_error.view.*
import mz.co.moovi.mpesalibui.R
import kotlinx.android.synthetic.main.mpesa_sdk_view_error.view.action_button as actionButton

class ErrorView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    init {
        inflate(context, R.layout.mpesa_sdk_view_error, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    fun render(
        @StringRes titleResId: Int,
        @StringRes descriptionResId: Int,
        @DrawableRes illustrationResId: Int?,
        onClickActionButton: (() -> Unit)? = null,
        @StringRes actionButtonTextResId: Int? = null
    ) {
        illustration.isGone = illustrationResId == null
        Glide.with(context).load(illustrationResId).into(illustration)

        title.text = resources.getString(titleResId)
        description.text = resources.getString(descriptionResId)

        onClickActionButton?.run {
            actionButtonTextResId?.run { actionButton.setText(this) }
            actionButton.setOnClickListener { this.invoke() }
        }
        actionButton.isGone = actionButtonTextResId == null
    }
}