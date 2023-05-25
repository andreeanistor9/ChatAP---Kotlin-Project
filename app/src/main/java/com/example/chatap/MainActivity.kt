package com.example.chatap

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var profileNameTextView: TextView
    private lateinit var bottomNavigationView: BottomNavigationView
    private var videoView: VideoView? = null
    private var mediaController: android.widget.MediaController? =null
    private lateinit var btnShare: Button
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileNameTextView = findViewById(R.id.profileName)

        videoView = findViewById(R.id.myVideoView)
        setUpVideoPlayer()
        btnShare = findViewById(R.id.btnShare)
        val url =  "https://www.youtube.com/watch?v=1AnrasgflFc&list=PPSV"
        btnShare.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, url)
            val chooser = Intent.createChooser(intent, "Share using...")
            startActivity(chooser)
        }

        mAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = mAuth.currentUser
        if(currentUser != null){
            val userName: String? = currentUser.displayName
            profileNameTextView.text = userName
        }



        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener{ item ->
            when(item.itemId){
                R.id.logout -> {
                    mAuth.signOut()
                    val intent = Intent(this@MainActivity, LogIn::class.java)
                    finish()
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.friends -> {
                    val intent = Intent(this@MainActivity, FriendsActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
    private fun setUpVideoPlayer() {
        if (mediaController == null) {
            mediaController = MediaController(this)
            mediaController!!.setAnchorView(this.videoView)
        }
        videoView!!.setMediaController(mediaController)
        videoView!!.setVideoURI(
            Uri.parse("android.resource://" + packageName + "/" + R.raw.intro)
        )
        videoView!!.requestFocus()
        videoView!!.pause()
        videoView!!.setOnCompletionListener {
            Toast.makeText(applicationContext, "Video Completed", Toast.LENGTH_SHORT).show()
        }
        videoView!!.setOnErrorListener { mp, what, extra ->
            Toast.makeText(applicationContext, "An Error Occured", Toast.LENGTH_SHORT).show()
            false
        }
    }
    override fun onResume() {
        super.onResume()

        val myView: View = findViewById(R.id.myView)
        val animator = ObjectAnimator.ofFloat(myView, "rotation", 0f, 360f)
        animator.duration = 3000 // Set the animation duration in milliseconds
       // animator.repeatCount = ObjectAnimator.INFINITE // Repeat the animation infinitely
        animator.start()
    }
}
