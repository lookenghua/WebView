package com.lookenghua.webview.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lookenghua.webview.page.HomePage

@Composable
fun WebViewNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "/") {
        composable("/") {
            HomePage(navController)
        }
    }
}