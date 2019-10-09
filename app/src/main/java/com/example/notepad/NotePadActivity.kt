package com.example.notepad

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_note_pad.*
import kotlinx.android.synthetic.main.rows.view.*


class NotePadActivity : AppCompatActivity()
{

    lateinit var ref : DatabaseReference
    lateinit var noteList : MutableList<Note>
    lateinit var listView: ListView

    lateinit var adapter:MyCustomAdapter


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_pad)
         listView = findViewById(R.id.listview)

        ref = FirebaseDatabase.getInstance().getReference("Note")
        noteList = mutableListOf()


        ref.addValueEventListener(object:ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError)
            {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    for(h in p0.children)
                    {
                        val note = h.getValue(Note::class.java)
                        noteList.add(note!!)
                    }
                   // listview.adapter = MyCustomAdapter(this@NotePadActivity,R.layout.rows,noteList,listView)

                    listView.adapter = MyCustomAdapter(this@NotePadActivity,R.layout.rows,noteList,listView)
                   // adapter.notifyDataSetChanged()

                    MyCustomAdapter(this@NotePadActivity,R.layout.rows,noteList,listView).notifyDataSetChanged()

                }
            }

        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        //Because the logout button is at the option menu toolbar
        // To add onClickListener we need to override this method
        if (item?.itemId == R.id.action_logout)
        {
            AlertDialog.Builder(this).apply {
                setTitle("Are you sure you want to logout?")
                //What are the 2 parameters?
                setPositiveButton("Yes") { _, _ ->
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@NotePadActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        //IF YOU don't do this, when user press back button user will see the register again
                    }
                    startActivity(intent)
                }
                setNegativeButton("Cancel") { _, _ ->

                }
            }.create().show()
        }
        else if(item?.itemId == R.id.addNote)
        {
            val intent = Intent(this,AddNoteActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    //By creating this inner class with the type BaseAdapter
    // We have to implement these methods in order to use it
    //private class MyCustomAdapter(context: Context) : BaseAdapter()
    inner class MyCustomAdapter(val mContext: Context,val layoutResId : Int,val noteList:List<Note>,val listView: ListView)
        : ArrayAdapter<Note>(mContext,layoutResId,noteList)
    {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
        {

            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(layoutResId,null)

            val titleViewName = rowMain.findViewById<TextView>(R.id.titleTv)
            val titles = noteList[position]
            titleViewName.text = titles.nodeTitle

            val TextViewBody = rowMain.findViewById<TextView>(R.id.description)
            val body = noteList[position]
            TextViewBody.text = body.nodeBody

            listView.invalidateViews()


            val temp = position+1

            titleViewName.setOnClickListener {
                when(position)
                {

                    position -> Toast.makeText(mContext,"Item $temp",Toast.LENGTH_SHORT).show()

                }
            }

            //delete button click
            rowMain.deleteBtn.setOnClickListener {


                deleteNote(titles)


            }
//
//            rowMain.editBtn.setOnClickListener {
//                //Go to function that handle edit of the notes/Array
//            }
//
//            rowMain.copyBtn.setOnClickListener {
//                val title = rowMain.titleTv.text.toString()
//                //Get description
//                val desc = rowMain.descEt.text.toString()
//                //concatinate
//                val s = title + "\n" + desc
//                //val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                //cb.text = s
//                Toast.makeText(mContext,"Copied",Toast.LENGTH_SHORT).show()
//            }

            return rowMain


        }

        private fun deleteNote(note: Note)
        {
            AlertDialog.Builder(mContext).apply {
                setTitle("Are you sure you want to delete?")
                //What are the 2 parameters?
                setPositiveButton("Yes") { _, _ ->
                    val myDatabase = FirebaseDatabase.getInstance().getReference("Note")
                    myDatabase.child(note.id).removeValue()

                    MyCustomAdapter(mContext,R.layout.rows,noteList,listView).clear()
                    //listView.adapter.notifyDataSetChanged()
                    MyCustomAdapter(this@NotePadActivity,R.layout.rows,noteList,listView).notifyDataSetChanged()

                    // Refresh activity after deleting last item
//                        finish()
//                        startActivity(getIntent())



                    Toast.makeText(mContext,"Deleted!",Toast.LENGTH_LONG).show()

                }
                setNegativeButton("Cancel") { _, _ ->

                }
            }.create().show()
        }



    }
}














