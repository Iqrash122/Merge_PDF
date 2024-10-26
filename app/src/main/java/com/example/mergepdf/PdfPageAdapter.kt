//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.pdf.PdfRenderer
//import android.os.ParcelFileDescriptor
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.core.content.FileProvider
//import androidx.recyclerview.widget.RecyclerView
//import com.example.mergepdf.PdfDetailActivity
//import com.example.mergepdf.R
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class PdfPageAdapter(
//    private var pdfFiles: List<File>,
//    private val onItemClick: (File) -> Unit
//) : RecyclerView.Adapter<PdfPageAdapter.PdfPageViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_page, parent, false)
//        return PdfPageViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
//        val pdfFile = pdfFiles[position]
//        holder.bind(pdfFile)
//        holder.itemView.setOnClickListener { onItemClick(pdfFile) }
//        holder.itemView.setOnClickListener {
//            val pdfUri = FileProvider.getUriForFile(holder.itemView.context, "${holder.itemView.context.packageName}.provider", pdfFile)
//            val title = pdfFile.nameWithoutExtension
//
//            val intent = Intent(holder.itemView.context, PdfDetailActivity::class.java).apply {
//                data = pdfUri
//                putExtra("EXTRA_PDF_TITLE", title)
//            }
//            holder.itemView.context.startActivity(intent)
//        }
//        holder.pdfTitleTextView.setOnClickListener {
//            showEditTitleDialog(holder.itemView.context, pdfFile)
//        }
//    }
//
//    override fun getItemCount(): Int = pdfFiles.size
//
//    // Overloaded method to handle updates with and without dateTime
//    fun updateFiles(newFiles: List<File>, dateTime: String? = null) {
//        pdfFiles = newFiles
//        notifyDataSetChanged()
//    }
//
//    private fun showEditTitleDialog(context: Context, pdfFile: File) {
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_title, null)
//        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
//        titleEditText.setText(pdfFile.nameWithoutExtension) // Set current title
//
//        AlertDialog.Builder(context)
//            .setTitle("Edit Title")
//            .setView(dialogView)
//
//            .setPositiveButton("Save") { _, _ ->
//                val newTitle = titleEditText.text.toString()
//                val newFile = File(pdfFile.parent, "$newTitle.pdf")
//                if (pdfFile.renameTo(newFile)) {
//                    // Update the file in the list and notify the adapter
//                    val updatedFiles = pdfFiles.map { if (it == pdfFile) newFile else it }
//                    updateFiles(updatedFiles)
//                } else {
//                    Toast.makeText(context, "Error renaming file", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//
//    class PdfPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val pageImageView: ImageView = itemView.findViewById(R.id.pageImageView)
//        val pdfTitleTextView: TextView = itemView.findViewById(R.id.pdfTitleTextView)
//        private val pdfDateTimeView: TextView = itemView.findViewById(R.id.pdfTimestampTextView)
////        val editTitleButton: Button = itemView.findViewById(R.id.editTitleButton)
//
//
//        fun bind(pdfFile: File) {
//            pdfTitleTextView.text = pdfFile.name
//
//            // Format the creation date and time for display
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd \n HH:mm:ss", Locale.getDefault())
//            val dateTime = dateFormat.format(Date(pdfFile.lastModified()))
//            pdfDateTimeView.text = "$dateTime"
//
//            // Optionally render a preview of the first page of the PDF
//            val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
//            val page = pdfRenderer.openPage(0)
//            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//            pageImageView.setImageBitmap(bitmap)
//            page.close()
//            pdfRenderer.close()
//        }
//    }
//
//
//}





import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.mergepdf.PdfDetailActivity
import com.example.mergepdf.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfPageAdapter(
    private var pdfFiles: List<File>,
    private val onItemClick: (File) -> Unit
) : RecyclerView.Adapter<PdfPageAdapter.PdfPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_page, parent, false)
        return PdfPageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        val pdfFile = pdfFiles[position]
        holder.bind(pdfFile)

        // OnClickListener for opening the PDF in detail activity
        holder.itemView.setOnClickListener {
            val pdfUri = FileProvider.getUriForFile(
                holder.itemView.context,
                "${holder.itemView.context.packageName}.provider",
                pdfFile
            )

            val title = pdfFile.nameWithoutExtension
            val intent = Intent(holder.itemView.context, PdfDetailActivity::class.java).apply {
                data = pdfUri
                putExtra("EXTRA_PDF_TITLE", title)
            }
            holder.itemView.context.startActivity(intent)
        }

        // Edit title on TextView click
        holder.pdfTitleTextView.setOnClickListener {
            showEditTitleDialog(holder.itemView.context, pdfFile)
        }
    }

    override fun getItemCount(): Int = pdfFiles.size

    // Method to update PDF files
    fun updateFiles(newFiles: List<File>, dateTime: String? = null) {
        pdfFiles = newFiles
        notifyDataSetChanged()
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

    class PdfPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val pageImageView: ImageView = itemView.findViewById(R.id.pageImageView)
        val pdfTitleTextView: TextView = itemView.findViewById(R.id.pdfTitleTextView)
        private val pdfDateTimeView: TextView = itemView.findViewById(R.id.pdfTimestampTextView)

        fun bind(pdfFile: File) {
            pdfTitleTextView.text = pdfFile.name

            // Format the creation date and time for display
            val dateFormat = SimpleDateFormat("yyyy-MM-dd \n HH:mm:ss", Locale.getDefault())
            val dateTime = dateFormat.format(Date(pdfFile.lastModified()))
            pdfDateTimeView.text = dateTime

            // Check if the file exists before trying to render the PDF
            if (pdfFile.exists()) {
                try {
                    val parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                    val page = pdfRenderer.openPage(0)

//                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
//                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//                    pageImageView.setImageBitmap(bitmap)

                    // Close the PDFRenderer
                    page.close()
                    pdfRenderer.close()
                    parcelFileDescriptor.close()
                } catch (e: Exception) {
                    Log.e("PdfPageAdapter", "Error rendering PDF: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                Log.e("PdfPageAdapter", "File not found: ${pdfFile.absolutePath}")
                Toast.makeText(itemView.context, "File not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
