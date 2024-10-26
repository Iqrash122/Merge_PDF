package com.example.mergepdf

import PdfPageAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PdfMerger
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity2 : AppCompatActivity() {




    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: SearchView
    private lateinit var pdfFiles: MutableList<File>
    private lateinit var pdfAdapter: pdfTablet
    private lateinit var hideKeyboard: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var deleteBtn : RelativeLayout
    private lateinit var topCardView: LinearLayoutCompat
    private lateinit var searchCardView: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewPdf: RelativeLayout
    private lateinit var cardView1: CardView
    private lateinit var cardView2: CardView
    private lateinit var recyclerItem : LinearLayoutCompat

    private  val PICK_PDF_FILE = 2


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedTheme()
        setContentView(R.layout.activity_main2)


        hideKeyboard = findViewById(R.id.main)
        hideKeyboard.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchBar.windowToken, 0)
            searchBar.clearFocus()

        }

        cardView1 = findViewById(R.id.cardview1)
        cardView2 = findViewById(R.id.cardview2)
        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val defaultColor = Color.parseColor("#0078D4")
        val color = sharedPref.getInt("SELECTED_COLOR", defaultColor)

        // Log the color to ensure it's being received correctly
        Log.d("MainActivity", "Received color: #${Integer.toHexString(color)}")

        // Set the initial CardView background color
        cardView1.setCardBackgroundColor(color)
        cardView2.setCardBackgroundColor(color)

        selectCard(cardView1, cardView2, color)

        // Set click listeners for the card views
        cardView1.setOnClickListener {
            selectCard(cardView1, cardView2, color)
            cardView2.visibility = View.VISIBLE // Show cardView2 if it's not the selected one
        }
        cardView2.setOnClickListener {
            selectCard(cardView2, cardView1, color)
            cardView1.visibility = View.VISIBLE // Show cardView1 if it's not the selected one
        }













        val greetingTextView = findViewById<AppCompatTextView>(R.id.greetingTextView)
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        // Update the text based on the time of day
        if (hourOfDay < 12) {
            greetingTextView.text = "Good Morning"
        } else {
            greetingTextView.text = "Good Afternoon"
        }




        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val menuIcon = findViewById<AppCompatImageView>(R.id.menu)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        val mergePdfButton: RelativeLayout = findViewById(R.id.mergePdfButton)
        mergePdfButton.setOnClickListener {
            // Perform action to merge PDFs

            // Optionally change the background to indicate selection
            mergePdfButton.setBackgroundResource(R.drawable.mergeview_pressed)

            // Reset the background after a delay (optional)
            mergePdfButton.postDelayed({
                mergePdfButton.setBackgroundResource(R.drawable.mergeview)
            }, 212000) // Delay in milliseconds
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            // Handle navigantion view item clicks here
            when (menuItem.itemId) {

                R.id.nav_setting ->{
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)

                }

                R.id.nav_privacy -> {
                    // Handle privacy action
                    val url = "https://webscare.com/privacy-policy-merge-pdf/"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
                R.id.nav_share -> {
                    // Handle share action
                    shareApp()

                }
                R.id.nav_rate -> {
                    // Handle rate us action
                    showRatingDialog()
                }

//                R.id.nav_dark_mode -> {
//                    showThemeSelectionDialog() // Open the dialog to choose theme
//                }
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }




        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.searchBar)
//        val mergePdfButton = findViewById<RelativeLayout>(R.id.mergePdfButton)
        progressBar = findViewById(R.id.progressBar)
        topCardView = findViewById(R.id.topCardView)
        searchCardView = findViewById(R.id.searchCardView)


        pdfFiles = loadPdfFiles() // Method to load all PDF files
        pdfAdapter = pdfTablet(pdfFiles) { file -> showOptionsDialog(file) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pdfAdapter

        enableSwipeToDelete()






//        pdfFiles = loadPdfFiles()
//        pdfAdapter = PdfPageAdapter(pdfFiles) { file -> showOptionsDialog(file) }
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = pdfAdapter


        searchCardView.setOnClickListener {
            // Open the search bar
            searchBar.isIconified = false
            searchBar.requestFocus()
        }

// Handle search queries
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchPdf(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchPdf(newText.orEmpty())
                return true
            }
        })

// Handle search bar focus changes
        searchBar.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Hide top card view and reposition search view
                topCardView.visibility = View.GONE
                val params = searchCardView.layoutParams as LinearLayout.LayoutParams
                params.topMargin = 16
                searchCardView.layoutParams = params
            } else {
                // Show top card view and reposition search view
                topCardView.visibility = View.VISIBLE
                val params = searchCardView.layoutParams as LinearLayout.LayoutParams
                params.topMargin = resources.getDimensionPixelSize(R.dimen.default_margin)
                searchCardView.layoutParams = params
            }
        }

        // Handle merge button click
        findViewById<RelativeLayout>(R.id.mergePdfButton).setOnClickListener { selectPdfFiles() }
        findViewById<RelativeLayout>(R.id.mergePdfButton1).setOnClickListener { selectAndViewPdf() }

    }

    private fun selectCard(selectedCard: CardView, unselectedCard: CardView, color: Int) {
        // Set the selected card's background color to the saved color (fully opaque)
        selectedCard.setCardBackgroundColor(color)

        // Reset the selected card's opacity to fully opaque (100%)
        selectedCard.alpha = 1f

        // Calculate 50% opacity for the unselected card color
        val unselectedColorWithOpacity = ColorUtils.setAlphaComponent(color, 128) // 128 is 50% opacity (range is 0-255)

        // Set the unselected card's background color (with 50% opacity)
        unselectedCard.setCardBackgroundColor(unselectedColorWithOpacity)

        // Optionally set the unselected card's opacity (visually adjust transparency)
        unselectedCard.alpha = 0.5f // Make the unselected card appear less prominent
    }


    private fun showThemeSelectionDialog() {
        val options = arrayOf("Dark Mode", "Light Mode", "System Default")
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val selectedTheme = sharedPreferences.getString("selectedTheme", "system") ?: "system" // Default value

        // Determine the selected position based on the saved preference
        var selectedPosition = when (selectedTheme) {
            "dark" -> 0
            "light" -> 1
            else -> 2
        }

        // Create AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Theme")

        // Create a LinearLayout to hold radio buttons
        val radioGroup = RadioGroup(this)
        radioGroup.orientation = RadioGroup.VERTICAL

        // Create RadioButtons dynamically for each theme option
        for (i in options.indices) {
            val radioButton = RadioButton(this)
            radioButton.text = options[i]
            radioButton.id = i // Assign a unique ID to each RadioButton

            // Set the checked state based on the selected position
            radioButton.isChecked = i == selectedPosition

            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(22, 0, 0, 0) // Left margin of 16dp, top/right/bottom margin of 0
            radioButton.layoutParams = params

            // Add the RadioButton to the RadioGroup
            radioGroup.addView(radioButton)
        }

        // Create the dialog
        val dialog = builder.setView(radioGroup)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .create()

        // Set an OnCheckedChangeListener to the RadioGroup to handle selection
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Update the selected position based on the radio button ID
            selectedPosition = checkedId

            // Apply the selected theme based on the position
            when (checkedId) {
                0 -> toggleDarkMode("dark")     // Dark Mode selected
                1 -> toggleDarkMode("light")    // Light Mode selected
                2 -> toggleDarkMode("system")   // System Default selected
            }

            // Save the selected theme to SharedPreferences
            with(sharedPreferences.edit()) {
                putString("selectedTheme", when (checkedId) {
                    0 -> "dark"
                    1 -> "light"
                    else -> "system"
                })
                apply()
            }

            dialog.dismiss() // Dismiss the dialog after selection
        }

        // Show the dialog
        dialog.show()
    }



    private fun enableSwipeToDelete() {
        val swipeToDeleteCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val fileToDelete = pdfFiles[position]

                // Store the file to restore in case the user presses undo
                showDeleteConfirmationDialog(fileToDelete, position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showDeleteConfirmationDialog(file: File, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete File")
            .setMessage("Are you sure you want to delete this file?")
            .setPositiveButton("Delete") { _, _ ->
                deleteFileWithUndoOption(file, position)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                pdfAdapter.notifyItemChanged(position) // Reset swipe if cancel is clicked
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteFileWithUndoOption(file: File, position: Int) {
        // Remove the file from the list
        pdfFiles.removeAt(position)
        pdfAdapter.notifyItemRemoved(position)

        // Show a Snackbar with undo option
        val snackbar = Snackbar.make(recyclerView, "File deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            // Add the file back to the list
            pdfFiles.add(position, file)
            pdfAdapter.notifyItemInserted(position)
        }

        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    // If the undo action wasn't clicked, permanently delete the file
                    permanentlyDeleteFile(file)
                }
            }
        })

        snackbar.show()
    }

    private fun permanentlyDeleteFile(file: File) {
        if (file.exists() && file.delete()) {
            Toast.makeText(this, "File permanently deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error deleting file", Toast.LENGTH_SHORT).show()
        }
    }



    private fun openMenu() {
        val popupMenu = PopupMenu(this, findViewById(R.id.menu))
        popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)
        popupMenu.show()
    }

    private fun selectPdfFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, 100)
    }

    private fun selectAndViewPdf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, 200) // Use a different requestCode, e.g., 200
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200 && resultCode == RESULT_OK) {
            // Handle the single PDF selection
            data?.data?.let { uri ->
                handleSelectedPdf(uri) // Convert Uri to File and view it
            }
        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uris = mutableListOf<Uri>()
            data?.let {
                if (it.clipData != null) {
                    val count = it.clipData?.itemCount ?: 0
                    for (i in 0 until count) {
                        it.clipData?.getItemAt(i)?.uri?.let { uri -> uris.add(uri) }

                    }

                } else {
                    it.data?.let { uri -> uris.add(uri) }
                }
            }
            if (uris.size > 1) {
                mergePdfFiles(uris)
            } else {
                Toast.makeText(this, "Please select at least two PDFs to merge", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun animateViewWidth(view: View, startWidth: Int, endWidth: Int, duration: Long) {
        val layoutParams = view.layoutParams
        val animator = ValueAnimator.ofInt(startWidth, endWidth).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val animatedWidth = animation.animatedValue as Int
                layoutParams.width = animatedWidth
                view.layoutParams = layoutParams
            }
            start()
        }
    }






    private fun mergePdfFiles(uris: List<Uri>) {
        progressBar.visibility = View.VISIBLE
        progressBar.max = uris.size
        var mergedFiles = 0

        val mergeProgressView = findViewById<View>(R.id.mergeProgressView)
        val initialWidth = resources.getDimensionPixelSize(R.dimen.initial_view_width) // 179dp in pixels
        val finalWidth = initialWidth // Width to animate to at the end of merging
        val zeroWidth = 0 // Width to animate to during merging

        // Animate view to 0dp at the start of merging
        animateViewWidth(mergeProgressView, initialWidth, zeroWidth, 500) // 500 ms animation

        // Show title input dialog before merging starts
        val defaultTitle = "Untitled"

        runOnUiThread {
            // Display title input dialog to the user
            showTitleInputDialog(defaultTitle) { title ->
                Thread {
                    try {
                        // Create a file in external storage
                        val mergedPdfFile = File(getExternalFilesDir(null), "$title.pdf")
                        val pdfDocument = PdfDocument(PdfWriter(mergedPdfFile))
                        val pdfMerger = PdfMerger(pdfDocument)

                        for (uri in uris) {
                            val inputStream = contentResolver.openInputStream(uri) ?: continue
                            val pdf = PdfDocument(PdfReader(inputStream))
                            pdfMerger.merge(pdf, 1, pdf.numberOfPages)
                            pdf.close()

                            mergedFiles++
                            // Update the progress bar and merge progress view on the main thread
                            runOnUiThread {
                                progressBar.progress = mergedFiles
                                val progressPercentage = (mergedFiles.toFloat() / uris.size * 100).toInt()
                                Toast.makeText(this, "Merging: $progressPercentage%", Toast.LENGTH_SHORT).show()
                            }
                        }

                        pdfDocument.close()

                        // Once the PDF is merged and saved, update the RecyclerView
                        runOnUiThread {
                            // Save the PDF file to be viewable
                            savePdfFile(mergedPdfFile)
                            animateViewWidth(mergeProgressView, zeroWidth, finalWidth, 2000) // 2000 ms animation
                            // Show rating dialog after merging is done
                            showRatingDialog()
                        }

                    } catch (e: Exception) {
                        // Handle exceptions
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Error merging PDFs: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }.start()
            }
        }
    }







    private fun showTitleInputDialog(defaultTitle: String,onTitleEntered: (String) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_title, null)

        val editText = dialogView.findViewById<EditText>(R.id.titleEditText)
        editText.setText(defaultTitle)


        AlertDialog.Builder(this)
            .setTitle("Enter PDF Title")
            .setView(dialogView) // Set the custom layout as the dialog's view
            .setPositiveButton("Save") { dialog, _ ->
                val title = editText.text.toString().trim()
                if (title.isNotEmpty()) {
                    // Pass the entered title back to the calling function
                    onTitleEntered(title)
                } else {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }



    private fun showRatingDialog() {
        AlertDialog.Builder(this)
            .setTitle("Rate Us")
            .setMessage("If you enjoy using our app, please take a moment to rate us. Your feedback helps us improve!")
            .setPositiveButton("Rate Now") { dialog, _ ->
                // Open the app's page on the Google Play Store
                val appPackageName = packageName
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
                dialog.dismiss()
            }
            .setNegativeButton("No, Thanks") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun savePdfFile(pdfFile: File) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateTime = dateFormat.format(Date())

        // Add the file to the list
        pdfFiles.add(pdfFile)
        // Sort the list by last modified date
        pdfFiles = pdfFiles.sortedByDescending { it.lastModified() }.toMutableList()
        // Notify adapter of the updated list
        pdfAdapter.updateFiles(pdfFiles, dateTime)
    }








    private fun loadPdfFiles(): MutableList<File> {
        val dir = getExternalFilesDir(null)
        return dir?.listFiles { _, name -> name.endsWith(".pdf") }?.toMutableList() ?: mutableListOf()
    }

    private fun updatePdfList() {
        pdfFiles = loadPdfFiles()
        pdfAdapter = pdfTablet(pdfFiles) { file -> showOptionsDialog(file) }
        recyclerView.adapter = pdfAdapter
    }

    private fun searchPdf(query: String) {
        val filteredFiles = pdfFiles.filter { it.name.contains(query, ignoreCase = true) }
        pdfAdapter.updateFiles(filteredFiles)
    }



    private fun showOptionsDialog(pdfFile: File) {
        val options = arrayOf("View", "Download", "Share", "Delete")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Options")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> viewPdf(pdfFile)    1 -> downloadPdf(pdfFile)
                2 -> sharePdf(pdfFile)   3 -> deletePdf(pdfFile)

            }
        }
        builder.show()
    }





    private fun handleSelectedPdf(uri: Uri) {
        try {
            // Use a content resolver to open an InputStream from the Uri
            val inputStream = contentResolver.openInputStream(uri)

            // If you need to create a temporary file to view it
            val tempFile = File.createTempFile("selected_pdf", ".pdf", cacheDir)
            val outputStream = tempFile.outputStream()

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Now open the temporary file using the viewPdf function
            viewPdf(tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to open the selected PDF", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getRealPathFromURI(uri: Uri): String? {
        var result: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
            if (idx >= 0) {
                result = cursor.getString(idx)
            }
            cursor.close()
        }
        return result
    }

    private fun viewPdf(pdfFile: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(FileProvider.getUriForFile(this@MainActivity2, "${applicationContext.packageName}.provider", pdfFile), "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }



    private fun downloadPdf(pdfFile: File) {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val downloadedFile = File(downloadDir, pdfFile.name)
        pdfFile.copyTo(downloadedFile, overwrite = true)
        Toast.makeText(this, "PDF downloaded to ${downloadedFile.absolutePath}", Toast.LENGTH_LONG).show()
    }

    private fun sharePdf(pdfFile: File) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@MainActivity2, "${applicationContext.packageName}.provider", pdfFile))
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, "Share PDF"))
    }

    private fun deletePdf(pdfFile: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete PDF")
            .setMessage("Are you sure you want to delete this PDF?")
            .setPositiveButton("Yes") { _, _ ->
                pdfFile.delete()
                updatePdfList()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" // You can also share images or other types by changing the type
            putExtra(Intent.EXTRA_SUBJECT, "Check out this amazing app!")
            putExtra(Intent.EXTRA_TEXT, "Download this awesome app from here: https://play.google.com/store/apps/details?id=your.package.name")
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    // Toggle dark mode based on user selection
    private fun toggleDarkMode(selectedMode: String) {
        when (selectedMode) {
            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveDarkModeState("dark")
            }
            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveDarkModeState("light")
            }
            "system" -> {
                // Set to follow system-wide dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                saveDarkModeState("system")
            }
        }
    }

    // Save the dark mode state in SharedPreferences
    private fun saveDarkModeState(mode: String) {
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("DarkMode", mode)
        editor.apply()
    }

    // Load the saved dark mode state from SharedPreferences
    private fun loadDarkModeState(): String? {
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        return prefs.getString("DarkMode", "system") // Default to system mode
    }

    // Apply the saved theme or follow the system-wide theme
    private fun applyTheme(darkModePreference: String?) {
        when (darkModePreference) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "system" -> {
                // Apply system mode and also check current system setting
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    // Apply the saved theme on activity launch
    private fun applySavedTheme() {
        val darkModePreference = loadDarkModeState()
        applyTheme(darkModePreference)
    }


    private fun openPdfWithExternalViewer(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Check if there's an app to handle the PDF
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No application found to open PDF", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}