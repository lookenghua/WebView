package com.lookenghua.webview.components

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import com.lookenghua.webview.bridge.WebAppInterface
import com.orhanobut.logger.Logger

@Composable
fun CustomWebView(url: String) {
    var webView: WebView? = null
    val context = LocalContext.current
    val webViewChromeClient = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            consoleMessage?.apply {
                val level = messageLevel()
                if (level == ConsoleMessage.MessageLevel.LOG) {
                    Logger.i(message())
                } else if (level == ConsoleMessage.MessageLevel.ERROR) {
                    Logger.e(message())
                }
            }
            return true
        }
    }
    val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context)).build()
    val webViewClient = object : WebViewClient() {
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            if (request != null) {
                return assetLoader.shouldInterceptRequest(request.url)
            }
            return null
        }

        // 开始载入页面调用
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        // 在页面加载结束时调用
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

        // 打开网页时不调用系统浏览器
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (null == request?.url) return false
            val showOverrideUrl = request.url.toString()
            try {
                if (!showOverrideUrl.startsWith("http://")
                    && !showOverrideUrl.startsWith("https://")
                ) {
                    //处理非http和https开头的链接地址
                    Intent(Intent.ACTION_VIEW, Uri.parse(showOverrideUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        view?.context?.applicationContext?.startActivity(this)
                    }
                    return true
                }
            } catch (e: Exception) {
                //没有安装和找到能打开(「xxxx://openlink.cc....」、「weixin://xxxxx」等)协议的应用
                return true
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        // 加载页面资源时调用
        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
        }

        // 加载页面的服务器出现错误时(如404调用)
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            when (error?.errorCode) {
                404 -> view?.loadUrl("")
            }
            super.onReceivedError(view, request, error)
        }

        // 处理https请求
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
        }

    }
    AndroidView(factory = { ctx ->
        WebView(ctx).apply {
            this.webViewClient = webViewClient
            this.webChromeClient = webViewChromeClient
            webView = this
            addJavascriptInterface(WebAppInterface(context), "NativeApi")
            settings.javaScriptEnabled = true // 开启javascript
            settings.allowFileAccess = true // 允许在File域下执行任意JavaScript代码

            settings.setAppCacheEnabled(true)

            settings.useWideViewPort = true // 将图片调整适合webview的大小
            settings.loadWithOverviewMode = true // 缩放至屏幕的大小

            settings.setSupportZoom(false) // 禁止缩放
            settings.loadsImagesAutomatically = true // 支持自动加载图片

            settings.domStorageEnabled = true // 开启DOM storage API功能
            settings.databaseEnabled = true // 开启database storage API功能
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = 0
            }
            loadUrl(url)
        }
    })
}