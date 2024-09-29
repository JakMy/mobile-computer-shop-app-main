package com.example.myshopapp.fragment

import Product
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myshopapp.ProductDetailActivity
import com.example.myshopapp.R
import com.example.myshopapp.adapter.ProductAdapter
import com.example.myshopapp.adapter.PromoPagerAdapter
import com.example.myshopapp.data.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

class HomeFragment : Fragment() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var promoPagerAdapter: PromoPagerAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var promoContainer: ViewPager2
    private lateinit var indicatorContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        database = FirebaseDatabase.getInstance()

        promoContainer = view.findViewById(R.id.promoContainer)
        indicatorContainer = view.findViewById(R.id.indicatorContainer)
        setupRecyclerView(view)
        setupPromoViewPager()
        loadProductsFromDatabase()
        return view
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        productAdapter = ProductAdapter()
        recyclerView.adapter = productAdapter
    }

    private fun setupPromoViewPager() {
        promoPagerAdapter = PromoPagerAdapter(emptyList()) { product ->
            // Obsługa kliknięcia na produkt w ViewPager
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("product", product as Serializable)  // Przekazanie obiektu Product jako Serializable
            startActivity(intent)
        }
        promoContainer.adapter = promoPagerAdapter
        promoContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setupIndicators(count: Int) {
        indicatorContainer.removeAllViews() // Usuwanie poprzednich indykatorów
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_inactive
                    )
                )
            }
            indicatorContainer.addView(indicators[i], layoutParams)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive)
                )
            }
        }
    }

    private fun loadProductsFromDatabase() {
        val productsRef = database.getReference("products")
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()
                val promoProducts = mutableListOf<Product>()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.key ?: continue
                    for (productSnapshot in categorySnapshot.children) {
                        val product = when (category) {
                            "case" -> productSnapshot.getValue(Case::class.java)?.apply { this.category = "case" }
                            "cooling" -> productSnapshot.getValue(Cooling::class.java)?.apply { this.category = "cooling" }
                            "graphic_card" -> productSnapshot.getValue(GraphicCard::class.java)?.apply { this.category = "graphic_card" }
                            "processor" -> productSnapshot.getValue(Processor::class.java)?.apply { this.category = "processor" }
                            "power_supply" -> productSnapshot.getValue(PowerSupply::class.java)?.apply { this.category = "power_supply" }
                            "motherboard" -> productSnapshot.getValue(Motherboard::class.java)?.apply { this.category = "motherboard" }
                            "memory" -> productSnapshot.getValue(Memory::class.java)?.apply { this.category = "memory" }
                            "ssd" -> productSnapshot.getValue(SSD::class.java)?.apply { this.category = "ssd" }
                            else -> null
                        }

                        product?.let {
                            it.id = productSnapshot.key ?: ""
                            Log.d("FirebaseMapping", "Mapped product: ${it.name} as ${it.javaClass.simpleName}")
                            if (it.sale > 0) {
                                promoProducts.add(it)
                            } else {
                                products.add(it)
                            }
                        }
                    }
                }
                productAdapter.submitList(products)
                promoPagerAdapter.updateProducts(promoProducts)
                setupIndicators(promoProducts.size) // Ustawienie indykatorów po załadowaniu produktów
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })
    }
}
