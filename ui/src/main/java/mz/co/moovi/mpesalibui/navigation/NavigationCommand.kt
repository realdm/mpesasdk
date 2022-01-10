package mz.co.moovi.mpesalibui.navigation

import androidx.navigation.NamedNavArgument

interface NavigationCommand {
    val destination: String
    val namedArgs: List<NamedNavArgument>
    fun createRoute(args: List<Any> = emptyList()): String
}