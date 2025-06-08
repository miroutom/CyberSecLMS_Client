package hse.diploma.cybersecplatform.ui.screens.code_editor

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MonacoEditor(
    initialCode: String,
    language: String = "javascript",
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var lastCode by remember { mutableStateOf(initialCode) }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    loadsImagesAutomatically = true
                }

                webViewClient =
                    object : WebViewClient() {
                        override fun onPageFinished(
                            view: WebView?,
                            url: String?,
                        ) {
                            initEditor(initialCode, language)
                            webView = this@apply
                        }
                    }

                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun onCodeChange(code: String) {
                            if (code != lastCode) {
                                lastCode = code
                                onCodeChange(code)
                            }
                        }
                    },
                    "Android",
                )

                loadUrl("file:///android_asset/monaco-editor/index.html")
            }
        },
        modifier = modifier,
        update = { view ->
            if (initialCode != lastCode) {
                view.initEditor(initialCode, language)
                lastCode = initialCode
            }
        },
    )
}

private fun WebView.initEditor(
    code: String,
    language: String,
) {
    evaluateJavascript(
        """
        try {
            if (typeof editor === 'undefined') {
                editor = monaco.editor.create(document.getElementById('container'), {
                    value: `${code.escapeForJs()}`,
                    language: '$language',
                    theme: 'vs-dark',
                    automaticLayout: true,
                    minimap: { enabled: true },
                    fontSize: 14
                });

                editor.onDidChangeModelContent(function() {
                    Android.onCodeChange(editor.getValue());
                });
            } else {
                editor.getModel().setValue(`${code.escapeForJs()}`);
                monaco.editor.setModelLanguage(editor.getModel(), '$language');
            }
        } catch(e) {
            console.error('Editor init error:', e);
        }
        """.trimIndent(),
        null,
    )
}

fun String.escapeForJs(): String {
    return this.replace("\\", "\\\\")
        .replace("`", "\\`")
        .replace("$", "\\$")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\"", "\\\"")
        .replace("'", "\\'")
}
