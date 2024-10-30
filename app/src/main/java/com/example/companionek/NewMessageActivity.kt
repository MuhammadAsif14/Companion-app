package com.example.companionek
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class NewMessageActivity : AppCompatActivity() {
    private lateinit var recyclerview_newmessage: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        recyclerview_newmessage=findViewById(R.id.recyclerview_newmessage)
        supportActionBar?.title = "Select User"
        fetchUsers()

    }
    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        var myId=FirebaseAuth.getInstance().currentUser!!.uid
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(Users::class.java)
                    if (user != null) {
                        //adding all users except self
                        if(!(user.userId.equals(myId))){
                            adapter.add(UserItem(user))

                        }

                    }
                    adapter.setOnItemClickListener{ item,view->

                        val userItem = item as UserItem

                        val intent=Intent(this@NewMessageActivity,ChatLogActivity::class.java)
                        intent.putExtra(USER_KEY, userItem.user)
                        startActivity(intent)
                        finish()
                    }
                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: Users): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val usernameTextView = viewHolder.itemView.findViewById<TextView>(R.id.username_textview_new_message)
        usernameTextView.text = user.userName
//        viewHolder.itemView.username_textview_new_message.text = user.username

//        Picasso.get().load(user.profilepic).into(viewHolder.itemView.imageview_new_message)

        // Ensure the view is cast to ImageView explicitly
        val imageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageview_new_message)
        Picasso.get().load(user.profilepic).placeholder(R.drawable.profile).into(imageView)

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}

// this is super tedious

//class CustomAdapter: RecyclerView.Adapter<ViewHolder> {
//  override fun onBindViewHolder(p0:, p1: Int) {
//    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//  }
//}
