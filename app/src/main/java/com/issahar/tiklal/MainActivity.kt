package com.issahar.tiklal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject


private const val SP_DARK_MODE = "SP_DARK_MODE"
private const val SP_OLD_COLORS = "SP_OLD_COLORS"
private const val SP_FONT_SIZE = "SP_FONT_SIZE"
private const val SP_LAST_POSITION = "SP_LAST_POSITION"
private const val SP_BOOKMARKS = "SP_BOOKMARKS"

// Feature flag: Set to true to show last position saving UI (auto-save toggle, continue reading, etc.)
private const val SHOW_LAST_POSITION_UI = false

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var webView: WebView
    
    private var currentMajor: Int = 0
    private var currentMinor: Int = 0
    private var currentMajorTitle: String = ""
    private var currentMinorTitle: String = ""

    private val bookmarksLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val major = result.data?.getIntExtra(BookmarksActivity.EXTRA_MAJOR, -1) ?: -1
            val minor = result.data?.getIntExtra(BookmarksActivity.EXTRA_MINOR, -1) ?: -1
            if (major >= 0 && minor >= 0) {
                navigateToPosition(major, minor)
            }
        }
    }

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = applicationContext.getSharedPreferences("Tiklal_SP", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        
        webView = findViewById(R.id.webView)
        
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")
        
        val url = buildUrl()
        webView.loadUrl(url)
        webView.webViewClient = client
    }

    private fun saveCurrentPosition() {
        val obj = JSONObject()
        obj.put("major", currentMajor)
        obj.put("minor", currentMinor)
        obj.put("majorTitle", currentMajorTitle)
        obj.put("minorTitle", currentMinorTitle)
        obj.put("timestamp", System.currentTimeMillis())
        prefs.edit().putString(SP_LAST_POSITION, obj.toString()).apply()
    }

    private fun navigateToPosition(major: Int, minor: Int) {
        webView.evaluateJavascript("navigateToPosition($major, $minor)", null)
    }

    private fun buildUrl(): String {
        val darkMode = "darkMode=${prefs.getBoolean(SP_DARK_MODE, false)}"
        val oldColors = "oldColors=${prefs.getBoolean(SP_OLD_COLORS, false)}"
        val fontSize = "fontSize=${prefs.getInt(SP_FONT_SIZE, 2)}"
        val showLastPosition = "showLastPositionUI=$SHOW_LAST_POSITION_UI"
        return "file:///android_asset/index.html?$darkMode&$fontSize&$oldColors&$showLastPosition"
    }

    private val client: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return if (request.url.scheme != "settings") {
                val intent = Intent(Intent.ACTION_VIEW, request.url)
                startActivity(intent)
                true
            }
            else {
                val darkMode = request.url.getQueryParameter("darkMode")
                val oldColors = request.url.getQueryParameter("oldColors")
                val fontSize = request.url.getQueryParameter("fontSize")
                if (darkMode != null)
                    prefs.edit().putBoolean(SP_DARK_MODE, darkMode.toBoolean()).apply()
                if (oldColors != null)
                    prefs.edit().putBoolean(SP_OLD_COLORS, oldColors.toBoolean()).apply()
                if (fontSize != null)
                    prefs.edit().putInt(SP_FONT_SIZE, fontSize.toInt()).apply()
                true
            }
        }
    }

    inner class AndroidBridge {
        @JavascriptInterface
        fun onPositionChanged(major: Int, minor: Int, majorTitle: String, minorTitle: String) {
            runOnUiThread {
                currentMajor = major
                currentMinor = minor
                currentMajorTitle = majorTitle
                currentMinorTitle = minorTitle
                
                // Always save position
                saveCurrentPosition()
            }
        }

        @JavascriptInterface
        fun onWebViewReady() {
            // Only restore last position if the feature is enabled
            if (!SHOW_LAST_POSITION_UI) return
            
            // Check if there's a saved position to restore
            val lastPosJson = prefs.getString(SP_LAST_POSITION, null)
            if (lastPosJson != null) {
                val bookmark = BookmarksActivity.parseBookmark(lastPosJson)
                bookmark?.let {
                    // Navigate to saved position
                    runOnUiThread {
                        navigateToPosition(it.major, it.minor)
                    }
                }
            }
        }

        @JavascriptInterface
        fun openBookmarksScreen() {
            runOnUiThread {
                val intent = Intent(this@MainActivity, BookmarksActivity::class.java)
                intent.putExtra(BookmarksActivity.EXTRA_CURRENT_MAJOR, currentMajor)
                intent.putExtra(BookmarksActivity.EXTRA_CURRENT_MINOR, currentMinor)
                intent.putExtra(BookmarksActivity.EXTRA_CURRENT_MAJOR_TITLE, currentMajorTitle)
                intent.putExtra(BookmarksActivity.EXTRA_CURRENT_MINOR_TITLE, currentMinorTitle)
                intent.putExtra(BookmarksActivity.EXTRA_SHOW_LAST_POSITION_UI, SHOW_LAST_POSITION_UI)
                intent.putExtra(BookmarksActivity.EXTRA_DARK_MODE, prefs.getBoolean(SP_DARK_MODE, false))
                bookmarksLauncher.launch(intent)
            }
        }

        @JavascriptInterface
        fun addBookmark() {
            runOnUiThread {
                // Load existing bookmarks
                val bookmarksJson = prefs.getString(SP_BOOKMARKS, "[]") ?: "[]"
                val bookmarksArray = JSONArray(bookmarksJson)
                
                // Check if bookmark already exists
                for (i in 0 until bookmarksArray.length()) {
                    val existing = bookmarksArray.getJSONObject(i)
                    if (existing.getInt("major") == currentMajor && 
                        existing.getInt("minor") == currentMinor) {
                        Toast.makeText(this@MainActivity, R.string.bookmark_exists, Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }
                }
                
                // Add new bookmark
                val bookmark = JSONObject()
                bookmark.put("major", currentMajor)
                bookmark.put("minor", currentMinor)
                bookmark.put("majorTitle", currentMajorTitle)
                bookmark.put("minorTitle", currentMinorTitle)
                bookmark.put("timestamp", System.currentTimeMillis())
                bookmarksArray.put(bookmark)
                
                prefs.edit().putString(SP_BOOKMARKS, bookmarksArray.toString()).apply()
                Toast.makeText(this@MainActivity, R.string.bookmark_added, Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun savePosition() {
            runOnUiThread {
                saveCurrentPosition()
                Toast.makeText(this@MainActivity, R.string.position_saved, Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun goToLastPosition() {
            val lastPosJson = prefs.getString(SP_LAST_POSITION, null)
            val bookmark = lastPosJson?.let { BookmarksActivity.parseBookmark(it) }
            runOnUiThread {
                if (bookmark != null) {
                    navigateToPosition(bookmark.major, bookmark.minor)
                } else {
                    // First use - go to beginning
                    navigateToPosition(0, 0)
                }
            }
        }
    }
}
