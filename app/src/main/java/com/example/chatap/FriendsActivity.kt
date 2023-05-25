package com.example.chatap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.SearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var searchView: SearchView
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_friends)


        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter = UserAdapter(this,userList)

        searchView = findViewById(R.id.userSearchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(msg)
                return false
            }
        })
        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener{ item ->
            when(item.itemId){
                R.id.logout -> {
                    mAuth.signOut()
                    val intent = Intent(this@FriendsActivity, LogIn::class.java)
                    finish()
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.friends -> {
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    val intent = Intent(this@FriendsActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }


            }
            false
        }

        mDbRef.child("user").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for(postSnapshot in snapshot.children) {

                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }


                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout) {
            mAuth.signOut()
            val intent = Intent(this@FriendsActivity, LogIn::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }

    private fun filter(text: String){
        var filteredList: ArrayList<User> = ArrayList()

        for(user in userList){
            if(user.name!!.contains(text,ignoreCase = true)) {
                filteredList.add(user)
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this,"No input for search...", Toast.LENGTH_SHORT).show()
        }
        adapter.setFilteredList(filteredList)
    }
}
