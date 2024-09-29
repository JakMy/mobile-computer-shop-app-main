package com.example.myshopapp

import Product
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.myshopapp.adapter.ImagePagerAdapter
import com.example.myshopapp.data.*
import com.example.myshopapp.`object`.Cart
import com.example.myshopapp.`object`.PCConfigurator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var favoriteButton: ImageButton
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Animacja tła
        val rootLayout = findViewById<RelativeLayout>(R.id.root_layout)
        val animDrawable = rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        // Przycisk "Go Back"
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Pobranie instancji FirebaseAuth i DatabaseReference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Pobranie produktu z Intent
        val product = intent.getSerializableExtra("product") as? Product
        Log.d("ProductDetailActivity", "Product type: ${product?.javaClass?.name}")

        if (product != null) {

            // Inicjalizacja przycisku ulubionych
            favoriteButton = findViewById(R.id.favoriteButton)

            // Sprawdź, czy produkt jest już w ulubionych
            checkIfFavorite(product.id)

            favoriteButton.setOnClickListener {
                toggleFavorite(product)
            }

            // Wyświetl ogólne informacje o produkcie
            findViewById<TextView>(R.id.productName).text = product.name
            findViewById<TextView>(R.id.productPrice).text = "${product.price - product.sale} zł"
            findViewById<TextView>(R.id.productBrand).text = product.brand

            val viewPager = findViewById<ViewPager2>(R.id.productImageViewPager)
            val imageUrls = product.image_urls ?: emptyList()
            val adapter = ImagePagerAdapter(imageUrls) { position ->
                // Przekierowanie do pełnoekranowej galerii
                val intent = Intent(this, FullScreenGalleryActivity::class.java)
                intent.putStringArrayListExtra("image_urls", ArrayList(imageUrls))
                intent.putExtra("position", position)
                startActivity(intent)
            }
            viewPager.adapter = adapter

            val indicatorsContainer = findViewById<LinearLayout>(R.id.indicatorsContainer)
            setupIndicators(indicatorsContainer, imageUrls.size)

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(indicatorsContainer, position)
                }
            })

            // Ukrywanie wszystkich kontenerów
            hideAllCategoryDetails()

            // Ręczna konwersja na odpowiednią podklasę
            when (product.category) {
                "case" -> {
                    val caseProduct = product as? Case
                    if (caseProduct != null) {
                        Log.d("ProductDetailActivity", "Product is of type Case")
                        findViewById<LinearLayout>(R.id.caseDetailsContainer).visibility = LinearLayout.VISIBLE
                        findViewById<TextView>(R.id.caseFormFactorTextView).text = "Form Factor: ${caseProduct.form_factor ?: "Unknown"}"
                        findViewById<TextView>(R.id.colorTextView).text = "Color: ${caseProduct.color ?: "Unknown"}"
                        findViewById<TextView>(R.id.powerSupplyTextView).text = "Power Supply: ${caseProduct.power_supply ?: "Unknown"} W"
                        findViewById<TextView>(R.id.maxGpuSizeTextView).text = "Max Graphic Card Size: ${caseProduct.max_size_of_graphic_card ?: "Unknown"} mm"
                        findViewById<TextView>(R.id.maxCoolingSizeTextView).text = "Max Cooling Size: ${caseProduct.max_size_of_cooling ?: "Unknown"} mm"
                    }
                }
                "cooling" -> {
                    val coolingProduct = product as? Cooling
                    if (coolingProduct != null) {
                        Log.d("ProductDetailActivity", "Product is of type Cooling")
                        findViewById<LinearLayout>(R.id.coolingDetailsContainer).visibility = LinearLayout.VISIBLE
                        findViewById<TextView>(R.id.typeTextView).text = "Type: ${coolingProduct.type ?: "Unknown"}"
                        findViewById<TextView>(R.id.fansTextView).text = "Fans: ${coolingProduct.fans ?: "Unknown"}"
                        findViewById<TextView>(R.id.radiatorSizeTextView).text = "Radiator Size: ${coolingProduct.radiator_size ?: "Unknown"} mm"
                    }
                }
                "graphic_card" -> {
                    val graphicsCardProduct = product as? GraphicCard
                    if (graphicsCardProduct != null) {
                        Log.d("ProductDetailActivity", "Product is of type GraphicsCard")
                        findViewById<LinearLayout>(R.id.graphicsCardDetailsContainer).visibility = LinearLayout.VISIBLE
                        findViewById<TextView>(R.id.coreClockTextView).text = "Core Clock: ${graphicsCardProduct.coreClock ?: "Unknown"}"
                        findViewById<TextView>(R.id.graphicCardBoostClockTextView).text = "Boost Clock: ${graphicsCardProduct.boostClock ?: "Unknown"}"
                        findViewById<TextView>(R.id.recommendedWattageTextView).text = "Recommended Wattage: ${graphicsCardProduct.recommendedWattage ?: "Unknown"} W"
                        findViewById<TextView>(R.id.sizeTextView).text = "Size: ${graphicsCardProduct.size ?: "Unknown"} mm"
                    }
                }
                "memory" -> {
                    val memoryProduct = product as? Memory
                    if (memoryProduct != null) {
                        Log.d("ProductDetailActivity", "Product is of type Memory")
                        findViewById<LinearLayout>(R.id.memoryDetailContainer).visibility = LinearLayout.VISIBLE
                        findViewById<TextView>(R.id.memoryTypeTextView).text = "Type: ${memoryProduct.type ?: "Unknown"}"
                        findViewById<TextView>(R.id.ramCapacityTextView).text = "Capacity: ${memoryProduct.capacity ?: "Unknown"}"
                        findViewById<TextView>(R.id.quantityTextView).text = "Quantity: ${memoryProduct.quantity ?: "Unknown"}"
                        findViewById<TextView>(R.id.speedTextView).text = "Speed: ${memoryProduct.speed ?: "Unknown"}"
                    }
                }
                "motherboard" -> {
                    val motherboardProduct = product as? Motherboard
                    if (motherboardProduct != null) {
                        Log.d("ProductDetailActivity", "Product is of type Motherboard")
                        findViewById<LinearLayout>(R.id.motherboardsDetailContainer).visibility = LinearLayout.VISIBLE
                        findViewById<TextView>(R.id.chipsetTextView).text = "Type of chipset: ${motherboardProduct.chipset ?: "Unknown"}"
                        findViewById<TextView>(R.id.motherboardFormFactorTextView).text = "Type of form factor: ${motherboardProduct.form_factor ?: "Unknown"}"
                        findViewById<TextView>(R.id.motherboardSocketTextView).text = "Type of socket: ${motherboardProduct.socket ?: "Unknown"}"
                        findViewById<TextView>(R.id.memorySlotsTextView).text = "Number of memory slots: ${motherboardProduct.memory_slots ?: "Unknown"}"
                        findViewById<TextView>(R.id.memoryStandardTextView).text = "Memory standard: ${motherboardProduct.memory_standard ?: "Unknown"}"
                    }
                }
                "power_supply" -> {
                    val powerSupplyProduct = product as? PowerSupply
                    if (powerSupplyProduct != null) {
                        Log.d("ProductDetailActivity", "Product is of type Power Supply")
                        findViewById<LinearLayout>(R.id.powerSupplyDetailContainer).visibility = LinearLayout.VISIBLE
                        findViewById<TextView>(R.id.wattageTextView).text = "Wattage: ${powerSupplyProduct.wattage ?: "Unknown"} W"
                        findViewById<TextView>(R.id.modularityTextView).text = "Modularity: ${powerSupplyProduct.modularity ?: "Unknown"}"
                        findViewById<TextView>(R.id.efficiencyRatingTextView).text = "Efficiency rating: ${powerSupplyProduct.efficiency_rating ?: "Unknown"}"
                    }
                }
                "processor" -> {
                    val processorProduct = product as? Processor
                    if (processorProduct != null) {
                        Log.d("ProductDetailActivity", "Product is of type Processor")
                        findViewById<LinearLayout>(R.id.processorDetailsContainer).visibility = LinearLayout.VISIBLE
                        findViewById<TextView>(R.id.coresTextView).text = "Number of cores: ${processorProduct.cores ?: "Unknown"}"
                        findViewById<TextView>(R.id.threadsTextView).text = "Number of threads: ${processorProduct.threads ?: "Unknown"}"
                        findViewById<TextView>(R.id.baseClockTextView).text = "Core clock: ${processorProduct.base_clock ?: "Unknown"}"
                        findViewById<TextView>(R.id.processorBoostClockTextView).text = "Boost clock: ${processorProduct.boost_clock ?: "Unknown"}"
                        findViewById<TextView>(R.id.processorSocketTextView).text = "Type of socket: ${processorProduct.socket ?: "Unknown"}"
                    }
                }
                "ssd" -> {
                    val ssdProduct = product as? SSD
                    if (ssdProduct != null) {
                        Log.d("ProductDetailActivity", "Product is of type SSD")
                        findViewById<LinearLayout>(R.id.ssdDetailsContainer).visibility = LinearLayout.VISIBLE
                        findViewById<TextView>(R.id.ssdCapacityTextView).text = "Capacity: ${ssdProduct.capacity ?: "Unknown"}"
                        findViewById<TextView>(R.id.interfaceTypeTextView).text = "Type of interface: ${ssdProduct.interfaceType ?: "Unknown"}"
                        findViewById<TextView>(R.id.ssdFormFactorTextView).text = "Type of form factor: ${ssdProduct.form_factor ?: "Unknown"}"
                    }
                }
                else -> {
                    Log.d("ProductDetailActivity", "Product category not recognized or missing")
                }
            }

            // Przycisk "Add to Cart"
            findViewById<Button>(R.id.addToCartButton).setOnClickListener {
                Cart.addProduct(product, 1)
                Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
            }

            // Przycisk "Buy Now"
            findViewById<Button>(R.id.buyNowButton).setOnClickListener {
                Cart.addProduct(product, 1)
                val intent = Intent(this, CheckoutActivity::class.java)
                startActivity(intent)
            }

            // Przycisk "Add to Configurator"
            findViewById<Button>(R.id.addToConfiguratorButton).setOnClickListener {
                val added = PCConfigurator.addProduct(product)
                if (added) {
                    Toast.makeText(this, "${product.name} added to configurator", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "${product.name} could not be added to configurator", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkIfFavorite(productId: String) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = database.child("users").child(userId).child("favorites").child(productId)

        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isFavorite = snapshot.exists()
                updateFavoriteButton()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProductDetailActivity", "Failed to check if product is favorite", error.toException())
            }
        })
    }

    private fun toggleFavorite(product: Product) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = database.child("users").child(userId).child("favorites").child(product.id)

        if (isFavorite) {
            favoriteRef.removeValue().addOnSuccessListener {
                isFavorite = false
                updateFavoriteButton()
                Toast.makeText(this, "${product.name} removed from favorites", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.e("ProductDetailActivity", "Failed to remove product from favorites", it)
            }
        } else {
            favoriteRef.setValue(product).addOnSuccessListener {
                isFavorite = true
                updateFavoriteButton()
                Toast.makeText(this, "${product.name} added to favorites", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.e("ProductDetailActivity", "Failed to add product to favorites", it)
            }
        }
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite)
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    // Ukrywa wszystkie kontenery z informacjami specyficznymi dla danej kategorii
    private fun hideAllCategoryDetails() {
        findViewById<LinearLayout>(R.id.caseDetailsContainer).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.coolingDetailsContainer).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.graphicsCardDetailsContainer).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.ssdDetailsContainer).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.processorDetailsContainer).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.powerSupplyDetailContainer).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.motherboardsDetailContainer).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.memoryDetailContainer).visibility = LinearLayout.GONE
    }

    private fun setupIndicators(container: LinearLayout, count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProductDetailActivity,
                        R.drawable.indicator_inactive
                    )
                )
                this.layoutParams = layoutParams
            }
            container.addView(indicators[i])
        }
        setCurrentIndicator(container, 0)
    }

    private fun setCurrentIndicator(container: LinearLayout, index: Int) {
        val childCount = container.childCount
        for (i in 0 until childCount) {
            val imageView = container.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}
