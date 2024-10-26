//package com.example.mergepdf
//
//import PdfPageAdapter
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.pdf.PdfRenderer
//import android.net.Uri
//import android.os.Bundle
//import android.os.Environment
//import android.os.ParcelFileDescriptor
//import android.widget.ImageView
//import android.widget.RelativeLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.FileProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class PdfDetailActivity : AppCompatActivity() {
//
//    private lateinit var pdfFile: File
//    private lateinit var pdfImageView: ImageView
//    private lateinit var pdfTitleTextView: TextView
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var pdfAdapter: PdfAdapter
//    private var pdfFiles = mutableListOf<File>()
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pdf_detail)
//
//        pdfImageView = findViewById(R.id.pdfImageView)
//        pdfTitleTextView = findViewById(R.id.pdfTitleTextView)
//        recyclerView = findViewById(R.id.recyclerecyclerViewrview)
//
//
//        pdfAdapter = PdfAdapter(pdfFiles) { file ->
//            // Handle item click here
//            Toast.makeText(this, "Clicked on ${file.name}", Toast.LENGTH_SHORT).show()
//            showOptionsDialog(file)
//        }
//        recyclerView.adapter = pdfAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        val share = findViewById<RelativeLayout>(R.id.share)
//        share.setOnClickListener {
//            sharePdf(pdfFile)
//        }
//
//        val view = findViewById<RelativeLayout>(R.id.view)
//        view.setOnClickListener {
//            viewPdf(pdfFile)
//        }
//
//        val download = findViewById<RelativeLayout>(R.id.download)
//        download.setOnClickListener {
//            downloadPdf(pdfFile)
//        }
//
//        val pdfUri = intent.data
//        val title = intent.getStringExtra("EXTRA_PDF_TITLE")
//
//        if (pdfUri != null) {
//            pdfTitleTextView.text = title // Set the title
//            pdfFile = File(cacheDir, "pdf_preview.pdf") // Initialize pdfFile
//            displayPdf(pdfUri)
//        } else {
//            Toast.makeText(this, "No PDF data available", Toast.LENGTH_SHORT).show()
//        }
//
//    }
//
//    private fun showOptionsDialog(pdfFile: File) {
//        val options = arrayOf("View", "Download", "Share", "Delete")
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Options")
//        builder.setItems(options) { _, which ->
//            when (which) {
//                0 -> viewPdf(pdfFile)
//                1 -> deletePdf(pdfFile)
//
//            }
//        }
//        builder.show()
//    }
//
//    private fun savePdfFile(pdfFile: File) {
//        val sharedPreferences = getSharedPreferences("pdf_prefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//
//        // Retrieve the existing list of file paths
//        val filePaths = sharedPreferences.getStringSet("pdf_file_paths", mutableSetOf()) ?: mutableSetOf()
//
//        // Add the new file path
//        filePaths.add(pdfFile.absolutePath)
//
//        // Save the updated list
//        editor.putStringSet("pdf_file_paths", filePaths)
//        editor.apply()
//
//        // Update the in-memory list and adapter
//        pdfFiles.add(pdfFile)
//        pdfFiles = pdfFiles.sortedByDescending { it.lastModified() }.toMutableList()
//        pdfAdapter.updateFiles(pdfFiles)
//    }
//
//
//    private fun deletePdf(pdfFile: File) {
//        AlertDialog.Builder(this)
//            .setTitle("Delete PDF")
//            .setMessage("Are you sure you want to delete this PDF?")
//            .setPositiveButton("Yes") { _, _ ->
//                pdfFile.delete()
//                updatePdfList()
//            }
//            .setNegativeButton("No", null)
//            .show()
//    }
//
//    private fun updatePdfList() {
//        pdfFiles = loadPdfFiles()
//        pdfAdapter = PdfAdapter(pdfFiles) { file -> showOptionsDialog(file) }
//        recyclerView.adapter = pdfAdapter
//    }
//
//    private fun loadPdfFiles(): MutableList<File> {
//        val sharedPreferences = getSharedPreferences("pdf_prefs", Context.MODE_PRIVATE)
//        val filePaths = sharedPreferences.getStringSet("pdf_file_paths", mutableSetOf()) ?: mutableSetOf()
//
//        return filePaths.map { File(it) }.filter { it.exists() }.toMutableList()
//    }
//
//
//    private fun displayPdf(pdfUri: Uri) {
//        try {
//            val inputStream = contentResolver.openInputStream(pdfUri) ?: return
//            pdfFile.outputStream().use { outputStream ->
//                inputStream.copyTo(outputStream)
//            }
//
//            ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
//                PdfRenderer(pfd).use { pdfRenderer ->
//                    val page = pdfRenderer.openPage(0)
//                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
//                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//                    pdfImageView.setImageBitmap(bitmap)
//                    page.close()
//                }
//            }
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error displaying PDF: ${e.message}", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun sharePdf(pdfFile: File) {
//        val uri = FileProvider.getUriForFile(this, "$packageName.provider", pdfFile)
//        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//            type = "application/pdf"
//            putExtra(Intent.EXTRA_STREAM, uri)
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        startActivity(Intent.createChooser(shareIntent, "Share PDF"))
//    }
//
//    private fun viewPdf(pdfFile: File) {
//        val uri = FileProvider.getUriForFile(this, "$packageName.provider", pdfFile)
//        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(uri, "application/pdf")
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        startActivity(viewIntent)
//    }
//
//    private fun downloadPdf(pdfFile: File) {
//        val destinationFile = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//            pdfFile.name
//        )
//
//        // Copy the file to the destination
//        pdfFile.inputStream().use { input ->
//            destinationFile.outputStream().use { output ->
//                input.copyTo(output)
//            }
//        }
//
//        // Notify the user
//        val downloadIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
//            data = Uri.fromFile(destinationFile)
//        }
//        sendBroadcast(downloadIntent)
//        Toast.makeText(this, "PDF downloaded", Toast.LENGTH_LONG).show()
//
//        // Notify RecyclerView to update
//        updateRecyclerView(destinationFile)
//    }
//
//    private fun updateRecyclerView(file: File) {
//        // Add the file to the list
//        pdfFiles.add(file)
//        pdfFiles = pdfFiles.sortedByDescending { it.lastModified() }.toMutableList()
//
//        // Notify the adapter
//        pdfAdapter.updateFiles(pdfFiles)
//    }
//}
//
//
//



package com.example.mergepdf

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfDetailActivity : AppCompatActivity() {

    private lateinit var pdfFile: File
    private lateinit var pdfImageView: ImageView
    private lateinit var pdfTitleTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfAdapter: PdfAdapter
    private var pdfFiles = mutableListOf<File>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_detail)

//        pdfImageView = findViewById(R.id.pageImageView)
        pdfTitleTextView = findViewById(R.id.pdfTitleTextView)
        recyclerView = findViewById(R.id.recyclerecyclerViewrview)
        val back  = findViewById<RelativeLayout>(R.id.relativeLayout)
        back.setOnClickListener {
            val i = Intent(this@PdfDetailActivity, MainActivity::class.java)
            startActivity(i)
        }

        pdfAdapter = PdfAdapter(pdfFiles) { file ->
//            Toast.makeText(this, "Clicked on ${file.name}", Toast.LENGTH_SHORT).show()
            showOptionsDialog(file)
        }
        recyclerView.adapter = pdfAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val share = findViewById<RelativeLayout>(R.id.share)
        share.setOnClickListener {
            sharePdf(pdfFile)
        }

        val view = findViewById<RelativeLayout>(R.id.view)
        view.setOnClickListener {
            viewPdf(pdfFile)
        }

        val download = findViewById<RelativeLayout>(R.id.download)
        download.setOnClickListener {
            downloadPdf(pdfFile)
        }

        val pdfUri = intent.data
        val title = intent.getStringExtra("EXTRA_PDF_TITLE")

        if (pdfUri != null) {
            pdfTitleTextView.text = title
            pdfFile = File(cacheDir, "pdf_preview.pdf")
            displayPdf(pdfUri)
        } else {
            Toast.makeText(this, "No PDF data available", Toast.LENGTH_SHORT).show()
        }

        // Load and display all PDF files
        updatePdfList()
    }

    private fun showOptionsDialog(pdfFile: File) {
        val options = arrayOf("View", "Download", "Share", "Delete")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Options")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> viewPdf(pdfFile)
                1 -> downloadPdf(pdfFile)
                2 -> sharePdf(pdfFile)
                3 -> deletePdf(pdfFile)
            }
        }
        builder.show()
    }

//    private fun savePdfFile(pdfFile: File) {
//        val sharedPreferences = getSharedPreferences("pdf_prefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//
//        // Retrieve the existing list of file paths
//        val filePaths = sharedPreferences.getStringSet("pdf_file_paths", mutableSetOf()) ?: mutableSetOf()
//
//        // Add the new file path
//        filePaths.add(pdfFile.absolutePath)
//
//        // Save the updated list
//        editor.putStringSet("pdf_file_paths", filePaths)
//        editor.apply()
//
//        // Update the in-memory list and adapter
////        updatePdfList()
//        pdfFiles.add(pdfFile)  // Add the new file to the list
//        pdfAdapter.notifyDataSetChanged()
//    }

    private fun savePdfFile(pdfFile: File) {
        val sharedPreferences = getSharedPreferences("pdf_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the existing list of file paths
        val filePaths = sharedPreferences.getStringSet("pdf_file_paths", mutableSetOf()) ?: mutableSetOf()

        // Add the new file path
        filePaths.add(pdfFile.absolutePath)

        // Save the updated list
        editor.putStringSet("pdf_file_paths", filePaths)
        editor.apply()

        // Update the in-memory list and adapter
        pdfFiles.add(pdfFile)
        pdfAdapter.notifyDataSetChanged()
    }



    private fun deletePdf(pdfFile: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete PDF")
            .setMessage("Are you sure you want to delete this PDF?")
            .setPositiveButton("Yes") { _, _ ->
                // Remove from file system
                if (pdfFile.delete()) {
                    // Remove from shared preferences
                    val sharedPreferences = getSharedPreferences("pdf_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    val filePaths = sharedPreferences.getStringSet("pdf_file_paths", mutableSetOf()) ?: mutableSetOf()
                    filePaths.remove(pdfFile.absolutePath)
                    editor.putStringSet("pdf_file_paths", filePaths)
                    editor.apply()

                    // Update list and adapter
                    updatePdfList()
                } else {
                    Toast.makeText(this, "Failed to delete PDF", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

//    private fun updatePdfList() {
//        pdfFiles = loadPdfFiles()
//        pdfAdapter.updateFiles(pdfFiles)
//    }

//    private fun updatePdfList() {
//        pdfFiles = loadPdfFiles()
//        pdfAdapter.updateFiles(pdfFiles)  // Ensure this method in PdfAdapter updates the list
//    }

//    private fun loadPdfFiles(): MutableList<File> {
//        val sharedPreferences = getSharedPreferences("pdf_prefs", Context.MODE_PRIVATE)
//        val filePaths = sharedPreferences.getStringSet("pdf_file_paths", mutableSetOf()) ?: mutableSetOf()
//
//        return filePaths.map { File(it) }.filter { it.exists() }.toMutableList()
//    }

//    private fun loadPdfFiles(): MutableList<File> {
//        val sharedPreferences = getSharedPreferences("pdf_prefs", Context.MODE_PRIVATE)
//        val filePaths = sharedPreferences.getStringSet("pdf_file_paths", mutableSetOf()) ?: mutableSetOf()
//
//        return filePaths.map { File(it) }.filter { it.exists() }.toMutableList()
//    }

    private fun loadPdfFiles(): MutableList<File> {
        val dir = getExternalFilesDir(null)
        return dir?.listFiles { _, name -> name.endsWith(".pdf") }?.toMutableList() ?: mutableListOf()
    }

    private fun updatePdfList() {
        pdfFiles = loadPdfFiles()
        pdfAdapter = PdfAdapter(pdfFiles) { file -> showOptionsDialog(file) }
        recyclerView.adapter = pdfAdapter
    }




    private fun displayPdf(pdfUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(pdfUri) ?: return
            pdfFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { pdfRenderer ->
                    val page = pdfRenderer.openPage(0)
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    pdfImageView.setImageBitmap(bitmap)
                    page.close()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error displaying PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun sharePdf(pdfFile: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", pdfFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share PDF"))
    }

    private fun viewPdf(pdfFile: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", pdfFile)
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(viewIntent)
    }

//    private fun downloadPdf(pdfFile: File) {
//        val destinationFile = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//            "${System.currentTimeMillis()}_${pdfFile.name}" // Ensure unique file name
//        )
//
//        // Copy the file to the destination
//        pdfFile.inputStream().use { input ->
//            destinationFile.outputStream().use { output ->
//                input.copyTo(output)
//            }
//        }
//
//        // Notify the user
//        val downloadIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
//            data = Uri.fromFile(destinationFile)
//        }
//        sendBroadcast(downloadIntent)
//        Toast.makeText(this, "PDF downloaded", Toast.LENGTH_LONG).show()
//
//        // Save to shared preferences and update the RecyclerView
//        savePdfFile(destinationFile)
//    }

    private fun downloadPdf(pdfFile: File) {
        val destinationFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "${System.currentTimeMillis()}_${pdfFile.name}" // Ensure unique file name
        )

        // Copy the file to the destination
        pdfFile.inputStream().use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Notify the user
        val downloadIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = Uri.fromFile(destinationFile)
        }
        sendBroadcast(downloadIntent)
        Toast.makeText(this, "PDF downloaded", Toast.LENGTH_LONG).show()

        // Save to shared preferences and update the RecyclerView
        savePdfFile(destinationFile)
    }

}
