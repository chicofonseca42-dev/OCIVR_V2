package pt.ocivr.app

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.HttpAuthHandler
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
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

        // Carrega as credenciais centralizadas
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

        // Suporte para toques, gestos de pinça e scroll livre
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        val defaultAgent = settings.userAgentString
        settings.userAgentString = defaultAgent.replace("; wv", "")

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        webView.webChromeClient = WebChromeClient()

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

                val desbloquearZoom = """
                    (function() {
                        var meta = document.querySelector('meta[name="viewport"]');
                        if (meta) {
                            meta.setAttribute('content', 'width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes');
                        }
                        document.body.style.overflowX = 'auto';
                        document.documentElement.style.overflowX = 'auto';
                    })();
                """.trimIndent()
                view?.evaluateJavascript(desbloquearZoom, null)

                if (user.isEmpty() || pass.isEmpty()) return

                val jsAuth = """
                    (function(){
                        var uVal = ${JSONObject.quote(user)};
                        var pVal = ${JSONObject.quote(pass)};

                        function setVal(el, val) {
                            if (!el || el.value === val) return;
                            var setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;
                            if (setter) {
                                setter.call(el, val);
                            } else {
                                el.value = val;
                            }
                            el.dispatchEvent(new Event('input', {bubbles: true}));
                            el.dispatchEvent(new Event('change', {bubbles: true}));
                        }

                        var ciclos = 0;
                        var interval = setInterval(function() {
                            ciclos++;
                            if (ciclos > 60) {
                                clearInterval(interval);
                                return;
                            }

                            var bodyTxt = document.body ? document.body.innerText : '';

                            // Proteção: Desligar dentro dos portais principais
                            if (bodyTxt.indexOf('Novo despiste') !== -1 || 
                                bodyTxt.indexOf('Insira ID de') !== -1 || 
                                bodyTxt.indexOf('HISTÓRICO') !== -1 ||
                                bodyTxt.indexOf('SERVIÇOS') !== -1 ||
                                bodyTxt.indexOf('Sair') !== -1 ||
                                bodyTxt.indexOf('Log off') !== -1 ||
                                bodyTxt.indexOf('Logout') !== -1 ||
                                bodyTxt.indexOf('Bem-vindo') !== -1 ||
                                bodyTxt.indexOf('Pesquisa') !== -1 ||
                                document.querySelector('a[href*="logout"], a[href*="logoff"], button[title*="sair"]')) {
                                clearInterval(interval);
                                return;
                            }

                            // 1. Procura a Password Visível
                            var camposPass = document.querySelectorAll('input[type="password"]');
                            var pEncontrada = null;
                            for (var j = 0; j < camposPass.length; j++) {
                                if (camposPass[j].offsetParent !== null) {
                                    pEncontrada = camposPass[j];
                                    break;
                                }
                            }

                            // 2. Procura o User Visível (Focando APENAS em campos reais de login)
                            var camposUser = document.querySelectorAll('#i0116, input[name="loginfmt"], input[name="USER"], #username, #j_username, input[name*="user" i], input[name*="login" i], input[type="email"]');
                            var uEncontrado = null;
                            for (var k = 0; k < camposUser.length; k++) {
                                var c = camposUser[k];
                                if (c.offsetParent !== null && c.type !== 'password' && c !== pEncontrada) {
                                    var idClass = (c.id + ' ' + c.className + ' ' + c.name).toLowerCase();
                                    if (idClass.indexOf('search') === -1 && idClass.indexOf('pesquisa') === -1) {
                                        uEncontrado = c;
                                        break;
                                    }
                                }
                            }

                            // -----------------------------------------------------------------
                            // LÓGICA DO SOUMEO & CPL WEB (Janela Única com User e Pass ao mesmo tempo)
                            // -----------------------------------------------------------------
                            if (uEncontrado && pEncontrada && !uEncontrado.__preenchido && !pEncontrada.__preenchido) {
                                setVal(uEncontrado, uVal);
                                setVal(pEncontrada, pVal);
                                uEncontrado.__preenchido = true;
                                pEncontrada.__preenchido = true;

                                setTimeout(function() {
                                    var btnLogin = document.querySelector('#logonFormSubmit, #submit, button[type="submit"], input[type="submit"], .urBtnStd, input[value*="Entrar" i], input[value*="Login" i], input[value*="logon" i], a[title*="logon" i]');
                                    if (!btnLogin) {
                                        var btns = document.querySelectorAll('button, input[type="button"], input[type="submit"], a, div[role="button"]');
                                        for (var b = 0; b < btns.length; b++) {
                                            var txt = (btns[b].innerText || btns[b].value || '').trim().toLowerCase();
                                            if (txt.indexOf('logon') !== -1 || txt.indexOf('entrar') !== -1) {
                                                btnLogin = btns[b];
                                                break;
                                            }
                                        }
                                    }

                                    if (btnLogin && btnLogin.offsetParent !== null) {
                                        btnLogin.click();
                                    } else if (pEncontrada.form) {
                                        pEncontrada.form.submit();
                                    }
                                    clearInterval(interval); // Termina imediatamente
                                }, 600);
                                return;
                            }

                            // -----------------------------------------------------------------
                            // LÓGICA DO NETQ (Fluxo em 3 Passos)
                            // -----------------------------------------------------------------

                            // PASSO 3: Janela apenas com Aviso Legal e Botão "Continuar"
                            if (bodyTxt.indexOf('Aviso Legal') !== -1 || bodyTxt.indexOf('utilizadores autorizados') !== -1) {
                                var todosBotoes = document.querySelectorAll('button, input[type="button"], input[type="submit"], a');
                                for (var idx = 0; idx < todosBotoes.length; idx++) {
                                    var btnCont = todosBotoes[idx];
                                    var texto = (btnCont.innerText || btnCont.value || '').trim().toLowerCase();
                                    if (texto === 'continuar' && btnCont.offsetParent !== null) {
                                        if (!btnCont.__clicked_aviso) {
                                            btnCont.__clicked_aviso = true;
                                            btnCont.click();
                                            clearInterval(interval); // Fim do fluxo NETQ
                                        }
                                        return;
                                    }
                                }
                            }

                            // PASSO 2: Janela com a Password visível (Mas SEM o campo do utilizador)
                            if (pEncontrada && !uEncontrado && !pEncontrada.__preenchido) {
                                setVal(pEncontrada, pVal);
                                pEncontrada.__preenchido = true;
                                
                                setTimeout(function() {
                                    var btnLogin = document.querySelector('#idSIButton9, #submit, button[type="submit"], input[type="submit"], input[value*="Entrar" i], input[value*="Login" i]');
                                    if (btnLogin && btnLogin.offsetParent !== null) {
                                        btnLogin.click();
                                    } else if (pEncontrada.form) {
                                        pEncontrada.form.submit();
                                    }
                                }, 3000); // Demora 3 segundos antes de clicar no Enter
                                return;
                            }

                            // PASSO 1: Janela APENAS com o Utilizador (SEM a password visível)
                            if (uEncontrado && !pEncontrada && !uEncontrado.__preenchido) {
                                setVal(uEncontrado, uVal);
                                uEncontrado.__preenchido = true;

                                setTimeout(function() {
                                    var btnNext = document.querySelector('#idSIButton9, #next, input[type="submit"], button[type="submit"], input[value*="Seguinte" i], input[value*="Next" i]');
                                    if (btnNext && btnNext.offsetParent !== null) {
                                        btnNext.click();
                                    } else if (uEncontrado.form) {
                                        uEncontrado.form.submit();
                                    }
                                }, 500); // 0.5s para avançar do utilizador para a password
                                return;
                            }

                        }, 500);
                    })();
                """.trimIndent()

                view?.evaluateJavascript(jsAuth, null)
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