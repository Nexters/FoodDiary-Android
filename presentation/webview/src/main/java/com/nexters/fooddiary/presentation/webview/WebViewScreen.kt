package com.nexters.fooddiary.presentation.webview

import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.nexters.fooddiary.core.ui.component.DetailScreenHeader

@Composable
fun WebViewScreen(
    url: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetUrl = url.toWebViewFriendlyUrl()
    val requestHeaders = mapOf("X-Requested-With" to "")

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
    ) {
        DetailScreenHeader(onBackButtonClick = onClose)
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.injectOverflowFixCss()
                            view?.postDelayed({ view.injectOverflowFixCss() }, 800)
                        }
                    }
                    webChromeClient = WebChromeClient()
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        useWideViewPort = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    }
                }
            },
            update = { webView ->
                if (webView.url != targetUrl) {
                    webView.loadUrl(targetUrl, requestHeaders)
                }
            }
        )
    }
}

private fun String.toWebViewFriendlyUrl(): String {
    val parsed = Uri.parse(this)
    val host = parsed.host.orEmpty()
    if (host == "www.notion.so") {
        val pathSegments = parsed.pathSegments
        if (pathSegments.size >= 2) {
            val workspace = pathSegments[0]
            val pageId = pathSegments[1]
            return "https://$workspace.notion.site/$pageId"
        }
    }
    return this
}

private fun WebView.injectOverflowFixCss() {
    val css = "html, body, * { overflow-y: auto !important; height: auto !important; }"
    val js = """
        (function() {
            var style = document.getElementById('fooddiary-overflow-fix');
            if (!style) {
                style = document.createElement('style');
                style.id = 'fooddiary-overflow-fix';
                document.head.appendChild(style);
            }
            style.textContent = '$css';
        })();
    """.trimIndent()
    evaluateJavascript(js, null)
}
