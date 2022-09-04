package kidsparadisepatur.octalgroup.`in`.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ashutosh.techathon.R
import com.ashutosh.techathon.model.InstitudeModel
import com.ashutosh.techathon.model.UserDataModel
import com.bumptech.glide.Glide


class SearchUsersAdapter(var c: Context, var list: ArrayList<UserDataModel>,val onClicked: (UserDataModel) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable{

    val searchableList = ArrayList<UserDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.row_chat_users, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return searchableList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(searchableList[position])

        holder.view.setOnClickListener {
            onClicked(searchableList[position])
        }

        Glide.with(c).load(searchableList[position].profile_image).circleCrop().placeholder(R.drawable.profile).into(holder.imgUser)

    }

    inner class MyProjects(var view: View) : RecyclerView.ViewHolder(view) {
        var txtUserName = view.findViewById<TextView>(R.id.txtUserName)
        var imgUser = view.findViewById<ImageView>(R.id.imgUser)
        fun bind(data: UserDataModel) {
            txtUserName.text = data.name.toString()
        }

    }



    override fun getFilter(): Filter {
        return object : Filter() {
            private val filterResults = FilterResults()

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                searchableList.clear()
                if (constraint.isNullOrBlank())
                {
                    searchableList.addAll(list)
                }
                else
                {
                    val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                    for (item in 0..list.size) {
                        if (list[item].name!!.toLowerCase().contains(filterPattern)) {
                            searchableList.add(list[item])
                        }
                    }
                }
                return filterResults.also {
                    it.values = searchableList
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }

}