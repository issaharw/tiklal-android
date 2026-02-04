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
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject


private const val SP_DARK_MODE = "SP_DARK_MODE"
private const val SP_OLD_COLORS = "SP_OLD_COLORS"
private const val SP_FONT_SIZE = "SP_FONT_SIZE"
private const val SP_AUTO_SAVE_POSITION = "SP_AUTO_SAVE_POSITION"
private const val SP_LAST_POSITION = "SP_LAST_POSITION"
private const val SP_BOOKMARKS = "SP_BOOKMARKS"

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var webView: WebView
    private lateinit var fabBookmark: FloatingActionButton
    
    private var currentMajor: Int = 0
    private var currentMinor: Int = 0
    private var currentMajorTitle: String = ""
    private var currentMinorTitle: String = ""
    private var pendingNavigation: Pair<Int, Int>? = null

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
        fabBookmark = findViewById(R.id.fabBookmark)
        
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")
        
        val url = buildUrl()
        webView.loadUrl(url)
        webView.webViewClient = client

        setupFab()
    }

    private fun setupFab() {
        fabBookmark.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.bookmark_menu, popup.menu)
            
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_add_bookmark -> {
                        addCurrentPositionAsBookmark()
                        true
                    }
                    R.id.action_save_position -> {
                        saveCurrentPosition()
                        Toast.makeText(this, R.string.position_saved, Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_view_bookmarks -> {
                        val intent = Intent(this, BookmarksActivity::class.java)
                        bookmarksLauncher.launch(intent)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun addCurrentPositionAsBookmark() {
        val bookmarksJson = prefs.getString(SP_BOOKMARKS, "[]") ?: "[]"
        val bookmarks = BookmarksActivity.parseBookmarks(bookmarksJson)
        
        // Check if bookmark already exists
        val exists = bookmarks.any { it.major == currentMajor && it.minor == currentMinor }
        if (exists) {
            Toast.makeText(this, R.string.bookmark_exists, Toast.LENGTH_SHORT).show()
            return
        }
        
        val newBookmark = Bookmark(
            major = currentMajor,
            minor = currentMinor,
            majorTitle = currentMajorTitle,
            minorTitle = currentMinorTitle
        )
        bookmarks.add(0, newBookmark) // Add to beginning
        
        val jsonArray = JSONArray()
        bookmarks.forEach { bookmark ->
            val obj = JSONObject()
            obj.put("major", bookmark.major)
            obj.put("minor", bookmark.minor)
            obj.put("majorTitle", bookmark.majorTitle)
            obj.put("minorTitle", bookmark.minorTitle)
            obj.put("timestamp", bookmark.timestamp)
            jsonArray.put(obj)
        }
        prefs.edit().putString(SP_BOOKMARKS, jsonArray.toString()).apply()
        
        Toast.makeText(this, R.string.bookmark_added, Toast.LENGTH_SHORT).show()
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
        val autoSave = "autoSave=${prefs.getBoolean(SP_AUTO_SAVE_POSITION, true)}"
        return "file:///android_asset/index.html?$darkMode&$fontSize&$oldColors&$autoSave"
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
                val autoSave = request.url.getQueryParameter("autoSavePosition")
                if (darkMode != null)
                    prefs.edit().putBoolean(SP_DARK_MODE, darkMode.toBoolean()).apply()
                if (oldColors != null)
                    prefs.edit().putBoolean(SP_OLD_COLORS, oldColors.toBoolean()).apply()
                if (fontSize != null)
                    prefs.edit().putInt(SP_FONT_SIZE, fontSize.toInt()).apply()
                if (autoSave != null)
                    prefs.edit().putBoolean(SP_AUTO_SAVE_POSITION, autoSave.toBoolean()).apply()
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
                
                // Auto-save position if enabled
                if (prefs.getBoolean(SP_AUTO_SAVE_POSITION, true)) {
                    saveCurrentPosition()
                }
            }
        }

        @JavascriptInterface
        fun onWebViewReady() {
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
    }
}
