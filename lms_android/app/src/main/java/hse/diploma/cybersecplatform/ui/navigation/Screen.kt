package hse.diploma.cybersecplatform.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Authorization : Screen("authorization")
    data object Registration : Screen("registration")
}