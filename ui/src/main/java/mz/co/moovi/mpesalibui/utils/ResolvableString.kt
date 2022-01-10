package mz.co.moovi.mpesalibui.utils

import android.content.Context

interface ResolvableString {
    fun resolve(context: Context): String
}

fun resolvableString(stringId: Int) = object : ResolvableString {
    override fun resolve(context: Context): String {
        return context.getString(stringId)
    }
}

fun resolvableString(stringId: Int, vararg args: Any) = object : ResolvableString {
    override fun resolve(context: Context): String {
        return context.getString(stringId, args)
    }
}