package com.example.myshopapp.fragment

import Product
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopapp.AppInformationActivity
import com.example.myshopapp.OrderHistoryActivity
import com.example.myshopapp.R
import com.example.myshopapp.adapter.FavoritesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsFragment : Fragment() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoritesList: MutableList<Product>
    private lateinit var database: DatabaseReference
    private lateinit var toggleFavoritesButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val currentUser = FirebaseAuth.getInstance().currentUser

        val ordersHistoryButton: Button = view.findViewById(R.id.ordersHistoryButton)

        ordersHistoryButton.setOnClickListener {
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        val appInfoButton: Button = view.findViewById(R.id.appInfoButton)
        appInfoButton.setOnClickListener{
            val intent = Intent(requireContext(), AppInformationActivity::class.java)
            startActivity(intent)
        }

        if (currentUser != null) {
            val email = currentUser.email
            emailTextView.text = email
        } else {
            emailTextView.text = "Not logged in"
        }

        // Inicjalizacja RecyclerView i adaptera
        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView)
        favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoritesList = mutableListOf()
        favoritesAdapter = FavoritesAdapter(favoritesList)
        favoritesRecyclerView.adapter = favoritesAdapter

        // Inicjalizacja ImageButton do rozwijania/zwijania RecyclerView
        toggleFavoritesButton = view.findViewById(R.id.toggleFavoritesButton)
        toggleFavoritesButton.setOnClickListener {
            if (favoritesRecyclerView.visibility == View.VISIBLE) {
                favoritesRecyclerView.visibility = View.GONE
                toggleFavoritesButton.setImageResource(R.drawable.ic_expand)
            } else {
                favoritesRecyclerView.visibility = View.VISIBLE
                toggleFavoritesButton.setImageResource(R.drawable.ic_collapse)
            }
        }

        // Pobieranie ulubionych produktów z bazy danych
        if (currentUser != null) {
            loadFavorites(currentUser.uid)
        }

        return view
    }

    private fun loadFavorites(userId: String) {
        database = FirebaseDatabase.getInstance().getReference("users/$userId/favorites")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoritesList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { favoritesList.add(it) }
                }
                favoritesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Obsługa błędu
            }
        })
    }
}
