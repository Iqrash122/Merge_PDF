package com.example.mergepdf

import PdfPageAdapter
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import java.io.File

class SearchActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: SearchView
    private lateinit var pdfFiles: MutableList<File>
    private lateinit var pdfAdapter: PdfPageAdapter
    private lateinit var hideKeyboard: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var deleteBtn : RelativeLayout
    private lateinit var topCardView: LinearLayoutCompat
    private lateinit var searchCardView: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewPdf: RelativeLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pdfAdapter
        
        val searchview = findViewById<SearchView>(R.id.SearchView)
    }
}