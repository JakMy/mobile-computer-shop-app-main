package com.example.myshopapp

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        val rootLayout = findViewById<ConstraintLayout>(R.id.root_layout2)
        val animDrawable = rootLayout.background as AnimationDrawable

        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailEt)
        val passwordEditText = findViewById<EditText>(R.id.passET)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPassEt)
        val signUpButton = findViewById<Button>(R.id.button)
        val signInTextView = findViewById<TextView>(R.id.textView)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 8 characters long and contain only letters and digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email, password)
        }

        signInTextView.setOnClickListener {
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!isValid) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun isValidPassword(password: String): Boolean {
        val isValid = password.length >= 8 && password.all { it.isLetterOrDigit() }
        if (!isValid) {
            Toast.makeText(this, "Password must be at least 8 characters long and contain only letters and digits", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Rejestracja powiodła się, możesz wykonać dodatkowe akcje, np. przejście do innego ekranu
                    val user: FirebaseUser? = auth.currentUser
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    // Przykład: Przejdź do głównego ekranu aplikacji
                    // val intent = Intent(this, MainActivity::class.java)
                    // startActivity(intent)
                    finish()
                } else {
                    // Rejestracja nie powiodła się, pokaż komunikat o błędzie
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
