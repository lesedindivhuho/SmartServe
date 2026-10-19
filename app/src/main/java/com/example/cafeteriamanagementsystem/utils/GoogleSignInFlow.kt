package com.example.cafeteriamanagementsystem.utils

import android.content.DialogInterface
import android.content.Intent
import android.text.InputType
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Google SSO for students. Google handles authentication; the only extra thing
 * we ask for is a Student ID the first time an account signs in.
 *
 * Must be constructed while the activity is being created (e.g. as a property
 * initializer) because it registers an activity result launcher.
 */
class GoogleSignInFlow(
    private val activity: AppCompatActivity,
    private val onSignedIn: (User) -> Unit
) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var progressDialog: AlertDialog? = null

    // The result code is ignored on purpose: Google reports configuration errors
    // (e.g. a missing SHA-1) as RESULT_CANCELED, and the real reason is in the intent.
    private val launcher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleSignInResult(result.data)
        }

    fun start() {
        val client = googleClient() ?: run {
            showError(
                "Google sign-in isn't set up",
                "The app is missing its Google web client ID. Enable Google sign-in in the " +
                        "Firebase console and download a new google-services.json."
            )
            return
        }

        // Clear any cached Google session so the account picker always appears.
        client.signOut().addOnCompleteListener {
            launcher.launch(client.signInIntent)
        }
    }

    private fun googleClient(): GoogleSignInClient? {
        val webClientId = activity.resources.getIdentifier(
            "default_web_client_id",
            "string",
            activity.packageName
        )
        if (webClientId == 0) return null

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(webClientId))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, options)
    }

    private fun handleSignInResult(data: Intent?) {
        val account = try {
            GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
        } catch (error: ApiException) {
            showGoogleError(error.statusCode)
            return
        }

        if (account?.idToken == null) {
            showError("Google sign-in failed", "Google did not return a sign-in token. Please try again.")
            return
        }

        showProgress("Signing you in with Google…")

        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.idToken, null))
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                if (firebaseUser == null) {
                    hideProgress()
                    showError("Google sign-in failed", "Firebase did not return a user. Please try again.")
                    return@addOnSuccessListener
                }

                val fullName = account.displayName.orEmpty()
                    .ifBlank { firebaseUser.displayName.orEmpty() }
                    .ifBlank { "Student" }
                loadProfile(firebaseUser, fullName)
            }
            .addOnFailureListener { error ->
                hideProgress()
                SessionManager.logout(activity)
                showError(
                    "Google sign-in failed",
                    error.localizedMessage ?: "Firebase could not sign you in with Google."
                )
            }
    }

    private fun showGoogleError(statusCode: Int) {
        when (statusCode) {
            GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> Unit

            CommonStatusCodes.DEVELOPER_ERROR -> showError(
                "Google sign-in isn't set up for this device",
                "Google rejected the sign-in (error 10). Add this app's SHA-1 fingerprint in " +
                        "Firebase console > Project settings > Your apps, then download the new " +
                        "google-services.json into the app folder and rebuild."
            )

            CommonStatusCodes.NETWORK_ERROR -> showError(
                "No connection",
                "Check your internet connection and try again."
            )

            else -> showError(
                "Google sign-in failed",
                "Something went wrong signing in with Google (error $statusCode). Please try again."
            )
        }
    }

    private fun loadProfile(firebaseUser: FirebaseUser, fullName: String) {
        FirebaseUserStore.findByUid(firebaseUser.uid)
            .addOnSuccessListener { document ->
                hideProgress()
                if (document.exists() && !document.getString("studentId").isNullOrBlank()) {
                    finishSignIn(FirebaseUserStore.toUser(document))
                } else {
                    promptForStudentId(firebaseUser, fullName)
                }
            }
            .addOnFailureListener { error ->
                hideProgress()
                SessionManager.logout(activity)
                showError(
                    "Couldn't load your profile",
                    error.localizedMessage ?: "Check your connection and try again."
                )
            }
    }

    private fun promptForStudentId(firebaseUser: FirebaseUser, fullName: String) {
        if (!activity.canShowDialogs()) return

        val density = activity.resources.displayMetrics.density
        val input = TextInputEditText(activity).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            setSingleLine(true)
        }
        val inputLayout = TextInputLayout(activity).apply {
            hint = "Student ID"
            addView(input)
        }
        val container = FrameLayout(activity).apply {
            val horizontal = (24 * density).toInt()
            setPadding(horizontal, (8 * density).toInt(), horizontal, 0)
            addView(inputLayout)
        }

        val email = firebaseUser.email.orEmpty()
        val signedInAs = if (email.isNotBlank()) " as $email" else ""

        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle("Signed in with Google")
            .setMessage(
                "Hi $fullName, you're signed in$signedInAs.\n\n" +
                        "Enter your Student ID to finish setting up your account."
            )
            .setView(container)
            .setCancelable(false)
            .setNegativeButton("Cancel") { _, _ -> SessionManager.logout(activity) }
            .setPositiveButton("Continue", null)
            .show()

        // Set after show() so a validation error doesn't dismiss the dialog.
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { button ->
            val studentId = input.text?.toString()?.trim().orEmpty()
            if (studentId.isEmpty()) {
                inputLayout.error = "Student ID is required"
                return@setOnClickListener
            }

            inputLayout.error = null
            button.isEnabled = false

            claimStudentId(
                firebaseUser = firebaseUser,
                fullName = fullName,
                studentId = studentId,
                onSaved = { user ->
                    dialog.dismiss()
                    showSetupComplete(user)
                },
                onError = { message ->
                    button.isEnabled = true
                    inputLayout.error = message
                }
            )
        }
    }

    private fun claimStudentId(
        firebaseUser: FirebaseUser,
        fullName: String,
        studentId: String,
        onSaved: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseUserStore.findByStudentId(studentId)
            .addOnSuccessListener { documents ->
                val existing = documents.documents.firstOrNull()
                if (existing != null && existing.id != firebaseUser.uid) {
                    onError("This Student ID is already linked to another account")
                    return@addOnSuccessListener
                }

                FirebaseUserStore.saveProfile(
                    firebaseUser = firebaseUser,
                    fullName = fullName,
                    studentId = studentId,
                    provider = "google.com"
                )
                    .addOnSuccessListener {
                        onSaved(
                            User(
                                fullName = fullName,
                                username = studentId,
                                password = "",
                                role = UserRole.STUDENT,
                                email = firebaseUser.email.orEmpty()
                            )
                        )
                    }
                    .addOnFailureListener {
                        onError("Could not save your Student ID. Try again.")
                    }
            }
            .addOnFailureListener {
                onError("Could not check the Student ID. Check your connection.")
            }
    }

    private fun showSetupComplete(user: User) {
        if (!activity.canShowDialogs()) return

        MaterialAlertDialogBuilder(activity)
            .setTitle("Account set up")
            .setMessage("Your Google account is now linked to Student ID ${user.username}.")
            .setCancelable(false)
            .setPositiveButton("Go to dashboard") { _, _ -> finishSignIn(user) }
            .show()
    }

    private fun finishSignIn(user: User) {
        SessionManager.currentUser = user
        onSignedIn(user)
    }

    private fun showProgress(message: String) {
        if (!activity.canShowDialogs()) return
        hideProgress()

        val density = activity.resources.displayMetrics.density
        val padding = (24 * density).toInt()
        val content = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(padding, padding, padding, padding)
            addView(ProgressBar(activity))
            addView(TextView(activity).apply {
                text = message
                setPadding(padding, 0, 0, 0)
            })
        }

        progressDialog = MaterialAlertDialogBuilder(activity)
            .setView(content)
            .setCancelable(false)
            .show()
    }

    private fun hideProgress() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun showError(title: String, message: String) {
        if (!activity.canShowDialogs()) return

        MaterialAlertDialogBuilder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun AppCompatActivity.canShowDialogs() = !isFinishing && !isDestroyed
}
