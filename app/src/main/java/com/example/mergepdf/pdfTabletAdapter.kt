package com.example.mergepdf



import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class pdfTabletAdapter(
    private var pdfFiles: List<File>,
    private val onItemClick: (File) -> Unit
) : RecyclerView.Adapter<pdfTabletAdapter.PdfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tablet, parent, false)
        return PdfViewHolder(view)
    }

    fun updateFiles(newFiles: List<File>) {
        pdfFiles = newFiles
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val pdfFile = pdfFiles[position]
        holder.bind(pdfFile)
        holder.itemView.setOnClickListener { onItemClick(pdfFile) }
        holder.pdfTitleTextView.setOnClickListener {
            showEditTitleDialog(holder.itemView.context, pdfFile)
        }
    }


    private fun showEditTitleDialog(context: Context, pdfFile: File) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_title, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
        titleEditText.setText(pdfFile.nameWithoutExtension) // Set current title

        AlertDialog.Builder(context)
            .setTitle("Edit Title")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = titleEditText.text.toString()
                val newFile = File(pdfFile.parent, "$newTitle.pdf")
                if (pdfFile.renameTo(newFile)) {
                    // Update the file in the list and notify the adapter
                    val updatedFiles = pdfFiles.map { if (it == pdfFile) newFile else it }
                    updateFiles(updatedFiles)
                } else {
                    Toast.makeText(context, "Error renaming file", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun getItemCount(): Int = pdfFiles.size

    // Overloaded method to handle updates with and without dateTime
//    fun updateFiles(newFiles: List<File>, dateTime: String? = null) {
//        pdfFiles = newFiles
//        notifyDataSetChanged()
//    }

    inner class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        private val pageImageView: ImageView = itemView.findViewById(R.id.pageImageView)
        val pdfTitleTextView: TextView = itemView.findViewById(R.id.pdfTitleTextView)
        private val pdfDateTimeView: TextView = itemView.findViewById(R.id.pdfTimestampTextView)

        fun bind(pdfFile: File) {
            pdfTitleTextView.text = pdfFile.name

            // Format the creation date and time for display
            val dateFormat = SimpleDateFormat("yyyy-MM-dd \n HH:mm:ss", Locale.getDefault())
            val dateTime = dateFormat.format(Date(pdfFile.lastModified()))
            pdfDateTimeView.text = "$dateTime"

            // Optionally render a preview of the first page of the PDF
//            try {
//                val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
//                val page = pdfRenderer.openPage(0)
//                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
//                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//                pageImageView.setImageBitmap(bitmap)
//                page.close()
//                pdfRenderer.close()
//            } catch (e: Exception) {
//                // Handle exception if PDF rendering fails
//                pageImageView.setImageDrawable(null)
//                Toast.makeText(itemView.context, "Error rendering PDF preview", Toast.LENGTH_SHORT).show()
//            }
        }
    }
}
