package com.issahar.tiklal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class BookmarksActivity : AppCompatActivity() {

    private lateinit var rvBookmarks: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var layoutLastPosition: LinearLayout
    private lateinit var tvLastPosition: TextView
    private lateinit var btnContinueReading: Button
    
    private var bookmarks: MutableList<Bookmark> = mutableListOf()
    private var lastPosition: Bookmark? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)

        rvBookmarks = findViewById(R.id.rvBookmarks)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        layoutLastPosition = findViewById(R.id.layoutLastPosition)
        tvLastPosition = findViewById(R.id.tvLastPosition)
        btnContinueReading = findViewById(R.id.btnContinueReading)

        rvBookmarks.layoutManager = LinearLayoutManager(this)

        loadBookmarks()
        loadLastPosition()
        updateUI()
    }

    private fun loadBookmarks() {
        val prefs = getSharedPreferences("Tiklal_SP", Context.MODE_PRIVATE)
        val bookmarksJson = prefs.getString(SP_BOOKMARKS, "[]") ?: "[]"
        bookmarks = parseBookmarks(bookmarksJson)
    }

    private fun loadLastPosition() {
        val prefs = getSharedPreferences("Tiklal_SP", Context.MODE_PRIVATE)
        val lastPosJson = prefs.getString(SP_LAST_POSITION, null)
        lastPosition = lastPosJson?.let { parseBookmark(it) }
    }

    private fun updateUI() {
        if (bookmarks.isEmpty()) {
            rvBookmarks.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
        } else {
            rvBookmarks.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
            rvBookmarks.adapter = BookmarksAdapter(bookmarks, 
                onItemClick = { bookmark -> navigateToBookmark(bookmark) },
                onDeleteClick = { position -> deleteBookmark(position) }
            )
        }

        lastPosition?.let { pos ->
            layoutLastPosition.visibility = View.VISIBLE
            tvLastPosition.text = "${pos.majorTitle}\n${pos.minorTitle}"
            btnContinueReading.setOnClickListener {
                navigateToBookmark(pos)
            }
        } ?: run {
            layoutLastPosition.visibility = View.GONE
        }
    }

    private fun navigateToBookmark(bookmark: Bookmark) {
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_MAJOR, bookmark.major)
        resultIntent.putExtra(EXTRA_MINOR, bookmark.minor)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun deleteBookmark(position: Int) {
        bookmarks.removeAt(position)
        saveBookmarks()
        updateUI()
    }

    private fun saveBookmarks() {
        val prefs = getSharedPreferences("Tiklal_SP", Context.MODE_PRIVATE)
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
    }

    companion object {
        const val SP_BOOKMARKS = "SP_BOOKMARKS"
        const val SP_LAST_POSITION = "SP_LAST_POSITION"
        const val EXTRA_MAJOR = "EXTRA_MAJOR"
        const val EXTRA_MINOR = "EXTRA_MINOR"

        fun parseBookmarks(json: String): MutableList<Bookmark> {
            val list = mutableListOf<Bookmark>()
            try {
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(Bookmark(
                        major = obj.getInt("major"),
                        minor = obj.getInt("minor"),
                        majorTitle = obj.getString("majorTitle"),
                        minorTitle = obj.getString("minorTitle"),
                        timestamp = obj.optLong("timestamp", 0)
                    ))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun parseBookmark(json: String): Bookmark? {
            return try {
                val obj = JSONObject(json)
                Bookmark(
                    major = obj.getInt("major"),
                    minor = obj.getInt("minor"),
                    majorTitle = obj.getString("majorTitle"),
                    minorTitle = obj.getString("minorTitle"),
                    timestamp = obj.optLong("timestamp", 0)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

data class Bookmark(
    val major: Int,
    val minor: Int,
    val majorTitle: String,
    val minorTitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

class BookmarksAdapter(
    private val bookmarks: List<Bookmark>,
    private val onItemClick: (Bookmark) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<BookmarksAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMajorTitle: TextView = view.findViewById(R.id.tvMajorTitle)
        val tvMinorTitle: TextView = view.findViewById(R.id.tvMinorTitle)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookmark_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmark = bookmarks[position]
        holder.tvMajorTitle.text = bookmark.majorTitle
        holder.tvMinorTitle.text = bookmark.minorTitle
        holder.itemView.setOnClickListener { onItemClick(bookmark) }
        holder.btnDelete.setOnClickListener { onDeleteClick(position) }
    }

    override fun getItemCount() = bookmarks.size
}
