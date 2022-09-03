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
import com.ashutosh.techathon.model.StreamModel


class SearchStreamAdapter(var c: Context, var list: ArrayList<StreamModel>, val onClicked: (StreamModel) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable{

    val searchableList = ArrayList<StreamModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val my_view = LayoutInflater.from(c).inflate(R.layout.row_search, parent, false)
        return MyProjects(my_view)
    }

    override fun getItemCount(): Int {
        return searchableList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyProjects).bind(searchableList[position].streamname)

        holder.view.setOnClickListener {
            onClicked(searchableList[position])
        }

    }

    inner class MyProjects(var view: View) : RecyclerView.ViewHolder(view) {
        var vname = view.findViewById<TextView>(R.id.txtName)
        fun bind(name: String) {
            vname.text = name.toString()
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
                        if (list[item].streamname!!.toLowerCase().contains(filterPattern)) {
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