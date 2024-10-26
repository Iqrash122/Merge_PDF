package com.example.mergepdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import yuku.ambilwarna.AmbilWarnaDialog

class Settings : AppCompatActivity() {

    private lateinit var themeMode: LinearLayoutCompat
    private lateinit var colorPicker: LinearLayoutCompat
    private var mDefaultColor: Int = Color.RED // Example default color

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val back = findViewById<AppCompatImageView>(R.id.back)
        back.setOnClickListener {
            val i = Intent(this@Settings, MainActivity::class.java)
            startActivity(i)
        }

        themeMode = findViewById(R.id.thememode)
        themeMode.setOnClickListener {
            showThemeSelectionDialog()
        }

        colorPicker = findViewById(R.id.pickcolor)
        colorPicker.setOnClickListener {
            openColorPicker()
        }
    }

    private fun openColorPicker() {
        val dialog = AmbilWarnaDialog(this, mDefaultColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                mDefaultColor = color // Update the default color

                // Save the selected color in SharedPreferences
                val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("SELECTED_COLOR", color)
                    apply() // or commit()
                }

                // Show the selected color in a Toast
                Toast.makeText(this@Settings, "Selected Color: #${Integer.toHexString(color).substring(2)}", Toast.LENGTH_SHORT).show()

                // Start the other activity with the selected color
                val intent = Intent(this@Settings, MainActivity::class.java)
                intent.putExtra("SELECTED_COLOR", color)
                startActivity(intent)
            }

            override fun onCancel(dialog: AmbilWarnaDialog?) {
                Toast.makeText(this@Settings, "Color selection canceled", Toast.LENGTH_SHORT).show()
            }
        })
        dialog.show()
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
        val builder = android.app.AlertDialog.Builder(this)
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
}
