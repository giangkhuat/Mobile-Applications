package com.ait.aitforum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.ait.aitforum.R
import com.ait.aitforum.data.Post
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.post_row.view.*

class PostsAdapter (private val context : Context, private val uid: String ) : RecyclerView.Adapter<PostsAdapter.ViewHolder>(){

    private var PostsList = mutableListOf<Post>()
    private var postKeys = mutableListOf<String>()

    private var lastIndex = -1

    inner class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvAuthor = itemView.tvAuthor
        val tvTitle = itemView.tvTitle
        val tvBody = itemView.tvBody
        val btnDelete = itemView.btnDelete
        val ivPhoto = itemView.idPhoto
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.post_row, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return PostsList.size
    }

    /* call for everyone line in the list so if we change any item, we update it here and the viewholder*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var post = PostsList[position]
        holder.tvAuthor.setText(post.author)
        holder.tvTitle.setText(post.title)
        holder.tvBody.setText(post.body)
        setAnimation(holder.itemView, position)

        if (post.imgUrl.isEmpty()) {
            holder.ivPhoto.visibility = View.GONE
        } else {
            holder.ivPhoto.visibility = View.VISIBLE
            Glide.with(context).load(post.imgUrl).into(holder.ivPhoto)
        }

        if (post.uid == uid) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener() {
                removePost(holder.adapterPosition)
            }
        } else {
            holder.btnDelete.visibility = View.GONE
        }

    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastIndex) {
            val animation = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastIndex = position
        }
    }

    fun addPost(post: Post, key: String) {
        PostsList.add(post)
        postKeys.add(key)
    }

    private fun removePost(index: Int) {
        /* remove sth from firebase cloud */
        FirebaseFirestore.getInstance().collection("posts").document(
            postKeys[index]
        ).delete()

        /* remove from recycler view */
        PostsList.removeAt(index)
        postKeys.removeAt(index)
        notifyItemRemoved(index)

        FirebaseStorage.getInstance().getReference().child(PostsList[index].imageName).delete()
    }



    /* sb else remove her or his message, we can get notification */
    fun removePostByKey(key: String) {
        val index = postKeys.indexOf(key)
        if (index != -1) {
            PostsList.removeAt(index)
            postKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

}