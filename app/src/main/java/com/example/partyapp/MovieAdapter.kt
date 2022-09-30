package com.example.partyapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.partyapp.fragment.DescriptionFragment
import kotlinx.android.synthetic.main.movie_item.view.*

class MovieAdapter(
    val movies: List<Movie>,
    val fragmentManager: FragmentManager,
    val pref: SharedPreferences
) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(
        itemView: View,
        val fragmentManager: FragmentManager,
        val pref: SharedPreferences
    ) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(movie: Movie) {
            itemView.movie_item_tv_name.text = movie.title
            itemView.descriprion.text = movie.description
            if (movie.picture != "")
                Glide.with(itemView.context).load(movie.picture).into(itemView.movie_item_poster)
            else
                Glide.with(itemView.context).load(R.drawable.ic_baseline_event_24).into(itemView.movie_item_poster)
            itemView.setOnClickListener() {

                val descriptionFragment = DescriptionFragment()
                val bundle = Bundle()
                bundle.putString("title", itemView.movie_item_tv_name.text.toString())
                bundle.putString("context", itemView.descriprion.text.toString())
                bundle.putInt("image", 0)
                descriptionFragment.arguments = bundle
                makeCurrentFragmentInMainWindow(
                    descriptionFragment,
                    "descriptionFragment",
                    fragmentManager
                )

            }
        }


        private fun makeCurrentFragmentInMainWindow(
            fragment: Fragment,
            name: String,
            fragmentManager: FragmentManager
        ) {
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.main_fragmnet, fragment)
                addToBackStack(name.toString())
                commit()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view, fragmentManager, pref)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return movies.size
    }


}