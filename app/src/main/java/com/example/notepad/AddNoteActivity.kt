package com.example.notepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNoteActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth
    lateinit var ref : DatabaseReference
    lateinit var titleEdit : EditText
    lateinit var bodyEdit : EditText
    lateinit var addBtn : Button
    //lateinit var db : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)


        mAuth = FirebaseAuth.getInstance()
        titleEdit = findViewById(R.id.titleEdit)
        bodyEdit = findViewById(R.id.bodyEdit)
        addBtn = findViewById(R.id.addBtn)

        addBtn.setOnClickListener {
             saveData()
        }
    }

    private fun saveData()
    {
        val title = titleEdit.text.toString().trim()
        val body = bodyEdit.text.toString().trim()

        if(title.isEmpty())
        {
            titleEdit.error = "Please enter a title!"
            titleEdit.requestFocus()
            return
        }
        if(body.isEmpty())
        {
            bodyEdit.error = "Please enter something!"
            bodyEdit.requestFocus()
            return
        }

        ref = FirebaseDatabase.getInstance().getReference("Note")
        val noteID  = ref!!.push().key!!
        val notes = Note(noteID,title,body)

        ref.child(noteID).setValue(notes).addOnCompleteListener {
            Toast.makeText(this,"Saved!",Toast.LENGTH_LONG).show()
            val intent = Intent(this,NotePadActivity::class.java)
            startActivity(intent)
        }




    }
}
