package mz.co.moovi.mpesalibui.navigation

import androidx.navigation.NamedNavArgument

object C2BNavCommands {
    val root = object : NavigationCommand {
        override val destination: String = "c2b_payment"
        override fun createRoute(args: List<Any>) = "c2b_payment"
        override val namedArgs: List<NamedNavArgument> = emptyList()
    }
}