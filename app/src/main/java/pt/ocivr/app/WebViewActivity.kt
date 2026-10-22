package pt.ocivr.app

import android.annotation.SuppressLint
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.HttpAuthHandler
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var user = ""
    private var pass = ""
    private var tentativasBasic = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("url")
        if (url == null) { finish(); return }

        // Carrega o utilizador e a password guardados no telemóvel
        val dados = Credenciais.obter(this)
        user = dados.first
        pass = dados.second

        webView = WebView(this)
        setContentView(webView)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true

        val defaultAgent = settings.userAgentString
        settings.userAgentString = defaultAgent.replace("; wv", "")

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    Toast.makeText(this@WebViewActivity, "Erro: ${error?.description}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onReceivedHttpAuthRequest(
                view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?
            ) {
                if (user.isNotEmpty() && tentativasBasic < 3) {
                    tentativasBasic++
                    view?.setHttpAuthUsernamePassword(host ?: "", realm ?: "", user, pass)
                    handler?.proceed(user, pass)
                } else {
                    handler?.cancel()
                }
            }

            override fun onPageFinished(view: WebView?, currentUrl: String?) {
                tentativasBasic = 0

                if (pass.isEmpty()) return

                val js = """
                    (function(){
                      function setInput(el, val) {
                        if (!el) return;
                        var setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;
                        setter.call(el, val);
                        el.dispatchEvent(new Event('input', {bubbles: true}));
                        el.dispatchEvent(new Event('change', {bubbles: true}));
                      }

                      function tentarLogin() {
                        var p = document.querySelector('input[type=password]') || 
                                document.querySelector('#password') ||
                                document.querySelector('#j_password') ||
                                document.querySelector('input[name*="pass" i]');

                        var u = document.querySelector('input[type=text]') ||
                                document.querySelector('input[type=email]') ||
                                document.querySelector('#username') ||
                                document.querySelector('#j_username') ||
                                document.querySelector('input[name*="user" i]') ||
                                document.querySelector('input[name*="login" i]');

                        var preencheuAlgo = false;

                        if (u && (!u.value || u.value.trim() === '')) {
                          setInput(u, ${JSONObject.quote(user)});
                          preencheuAlgo = true;
                        }

                        if (p) {
                          if (!p.value || p.value.length === 0) {
                            setInput(p, ${JSONObject.quote(pass)});
                            preencheuAlgo = true;
                          }

                          setTimeout(function() {
                            var btn = document.querySelector('#submit') ||
                                      document.querySelector('#logonFormSubmit') ||
                                      document.querySelector('button[type=submit]') || 
                                      document.querySelector('input[type=submit]') ||
                                      document.querySelector('.urBtnStd') ||
                                      document.querySelector('input[value*="Entrar" i]') ||
                                      document.querySelector('input[value*="Login" i]');
                            if (btn) {
                              btn.click();
                            } else if (p.form) {
                              p.form.submit();
                            }
                          }, 500);
                        } else if (u && preencheuAlgo) {
                          setTimeout(function() {
                            var nextBtn = document.querySelector('#next') ||
                                          document.querySelector('button[type=submit]') || 
                                          document.querySelector('input[type=submit]');
                            if (nextBtn) nextBtn.click();
                          }, 500);
                        }
                      }

                      setTimeout(tentarLogin, 1000);
                      setTimeout(tentarLogin, 2500);
                    })();
                """.trimIndent()

                view?.evaluateJavascript(js, null)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })

        webView.loadUrl(url)
    }
}