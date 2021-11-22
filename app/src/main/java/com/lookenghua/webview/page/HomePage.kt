package com.lookenghua.webview.page

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lookenghua.webview.components.CustomWebView

@Composable
fun HomePage(navController: NavController) {
    CustomWebView("file:///android_asset/demo/index.html")
}

@Preview
@Composable
fun DefaultHomePagePreview() {
    val navController = rememberNavController()
    HomePage(navController = navController)
}