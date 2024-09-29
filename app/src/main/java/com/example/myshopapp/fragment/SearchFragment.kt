package com.example.myshopapp.fragment

import Product
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopapp.CheckoutActivity
import com.example.myshopapp.ProductDetailActivity
import com.example.myshopapp.R
import com.example.myshopapp.adapter.SearchAdapter
import com.example.myshopapp.data.*
import com.example.myshopapp.`object`.Cart
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var filterButton: Button
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var sortSpinner: Spinner

    private lateinit var database: DatabaseReference
    private var productsList: List<Product> = emptyList()

    // Zmienna do przechowywania przefiltrowanych produktów
    private var filteredProducts: List<Product> = emptyList()

    private lateinit var filterDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        filterButton = view.findViewById(R.id.filterButton)
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        sortSpinner = view.findViewById(R.id.sortSpinner)

        searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SearchAdapter(
            products = emptyList(),
            onProductClick = { product ->
                val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onAddToCartClick = { product ->
                Cart.addProduct(product, 1)
                Toast.makeText(requireContext(), "${product.name} added to cart", Toast.LENGTH_SHORT).show()
            },
            onBuyNowClick = { product ->
                Cart.addProduct(product, 1)
                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                intent.putExtra("buyNowProduct", product)
                startActivity(intent)
            }
        )
        searchResultsRecyclerView.adapter = searchAdapter

        // Inicjalizacja Spinnera do sortowania
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> sortProductsByName()
                    1 -> sortProductsByPriceAscending()
                    2 -> sortProductsByPriceDescending()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        searchButton.setOnClickListener {
            performSearch(searchEditText.text.toString())
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                performSearch(s.toString())
            }
        })

        filterButton.setOnClickListener {
            showFilterDialog()
        }

        database = FirebaseDatabase.getInstance().reference.child("products")
        loadProductsFromDatabase()

        return view
    }

    private fun performSearch(query: String) {
        val result = filterProductsByQuery(query)
        when (sortSpinner.selectedItemPosition) {
            0 -> searchAdapter.updateData(result.sortedBy { it.name })
            1 -> searchAdapter.updateData(result.sortedBy { it.price })
            2 -> searchAdapter.updateData(result.sortedByDescending { it.price })
            else -> searchAdapter.updateData(result)
        }
        searchResultsRecyclerView.visibility = if (result.isNotEmpty()) View.VISIBLE else View.GONE
    }


    private fun loadProductsFromDatabase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedProducts = mutableListOf<Product>()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.key ?: continue
                    for (productSnapshot in categorySnapshot.children) {
                        val product = when (category) {
                            "case" -> productSnapshot.getValue(Case::class.java)
                            "cooling" -> productSnapshot.getValue(Cooling::class.java)
                            "graphic_card" -> productSnapshot.getValue(GraphicCard::class.java)
                            "processor" -> productSnapshot.getValue(Processor::class.java)
                            "power_supply" -> productSnapshot.getValue(PowerSupply::class.java)
                            "motherboard" -> productSnapshot.getValue(Motherboard::class.java)
                            "memory" -> productSnapshot.getValue(Memory::class.java)
                            "ssd" -> productSnapshot.getValue(SSD::class.java)
                            else -> null
                        }

                        product?.let {
                            it.id = productSnapshot.key ?: ""
                            it.category = category
                            loadedProducts.add(it)
                        }
                    }
                }
                productsList = loadedProducts
                filteredProducts = productsList
                performSearch(searchEditText.text.toString()) // Aktualizacja widoku po załadowaniu produktów
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showFilterDialog() {
        filterDialog = Dialog(requireContext())
        filterDialog.setContentView(R.layout.fragment_search_filters)

        // Inicjalizacja CheckBoxów kategorii i przypisanie ich do odpowiednich kategorii
        val categoryCheckboxes = listOf(
            filterDialog.findViewById<CheckBox>(R.id.checkboxGraphicsCards) to "graphic_card",
            filterDialog.findViewById<CheckBox>(R.id.checkboxProcessors) to "processor",
            filterDialog.findViewById<CheckBox>(R.id.checkboxMotherboards) to "motherboard",
            filterDialog.findViewById<CheckBox>(R.id.checkboxMemory) to "memory",
            filterDialog.findViewById<CheckBox>(R.id.checkboxSSDs) to "ssd",
            filterDialog.findViewById<CheckBox>(R.id.checkboxCooling) to "cooling",
            filterDialog.findViewById<CheckBox>(R.id.checkboxCases) to "case",
            filterDialog.findViewById<CheckBox>(R.id.checkboxPowerSupplies) to "power_supply"
        )

        // Inicjalizacja CheckBoxów marek (podobnie jak dla kategorii)
        val brandCheckboxes = listOf(
            filterDialog.findViewById<CheckBox>(R.id.checkboxCoolerMaster) to "CoolerMaster",
            filterDialog.findViewById<CheckBox>(R.id.checkboxGembird) to "Gembird",
            filterDialog.findViewById<CheckBox>(R.id.checkboxEndorfy) to "Endorfy",
            filterDialog.findViewById<CheckBox>(R.id.checkboxGenesis) to "Genesis",
            filterDialog.findViewById<CheckBox>(R.id.checkboxKrux) to "Krux",
            filterDialog.findViewById<CheckBox>(R.id.checkboxMSI) to "MSI",
            filterDialog.findViewById<CheckBox>(R.id.checkboxAsus) to "Asus",
            filterDialog.findViewById<CheckBox>(R.id.checkboxGigabyte) to "Gigabyte",
            filterDialog.findViewById<CheckBox>(R.id.checkboxBeQuiet) to "BeQuiet",
            filterDialog.findViewById<CheckBox>(R.id.checkboxCryorig) to "Cryorig",
            filterDialog.findViewById<CheckBox>(R.id.checkboxDeepcool) to "Deepcool",
            filterDialog.findViewById<CheckBox>(R.id.checkboxSilentiumPC) to "SilentiumPC",
            filterDialog.findViewById<CheckBox>(R.id.checkboxFractalDesign) to "FractalDesign",
            filterDialog.findViewById<CheckBox>(R.id.checkboxCorsair) to "Corsair",
            filterDialog.findViewById<CheckBox>(R.id.checkboxGSkill) to "GSkill",
            filterDialog.findViewById<CheckBox>(R.id.checkboxGoodRam) to "GoodRam",
            filterDialog.findViewById<CheckBox>(R.id.checkboxKingston) to "Kingston",
            filterDialog.findViewById<CheckBox>(R.id.checkboxLexar) to "Lexar",
            filterDialog.findViewById<CheckBox>(R.id.checkboxThermaltake) to "Thermaltake",
            filterDialog.findViewById<CheckBox>(R.id.checkboxAMD) to "AMD",
            filterDialog.findViewById<CheckBox>(R.id.checkboxIntel) to "Intel",
            filterDialog.findViewById<CheckBox>(R.id.checkboxADATA) to "ADATA",
            filterDialog.findViewById<CheckBox>(R.id.checkboxSamsung) to "Samsung"
        )

        // Inicjalizacja przycisków toggle
        val toggleCategoryButton: ImageButton = filterDialog.findViewById(R.id.toggleCategoryButton)
        val toggleBrandButton: ImageButton = filterDialog.findViewById(R.id.toggleBrandButton)

        // Inicjalizacja kontenerów
        val categoryCheckboxContainer = filterDialog.findViewById<LinearLayout>(R.id.categoryCheckboxContainer)
        val brandCheckboxContainer = filterDialog.findViewById<LinearLayout>(R.id.brandCheckboxContainer)

        // Inicjalizacja obsługi toggle dla kategorii
        toggleCategoryButton.setOnClickListener {
            if (categoryCheckboxContainer.visibility == View.VISIBLE) {
                categoryCheckboxContainer.visibility = View.GONE
                toggleCategoryButton.setImageResource(R.drawable.ic_expand) // Ustaw ikonę rozwijania
            } else {
                categoryCheckboxContainer.visibility = View.VISIBLE
                toggleCategoryButton.setImageResource(R.drawable.ic_collapse) // Ustaw ikonę zwijania
            }
        }

        // Inicjalizacja obsługi toggle dla marek
        toggleBrandButton.setOnClickListener {
            if (brandCheckboxContainer.visibility == View.VISIBLE) {
                brandCheckboxContainer.visibility = View.GONE
                toggleBrandButton.setImageResource(R.drawable.ic_expand) // Ustaw ikonę rozwijania
            } else {
                brandCheckboxContainer.visibility = View.VISIBLE
                toggleBrandButton.setImageResource(R.drawable.ic_collapse) // Ustaw ikonę zwijania
            }
        }

        // Inicjalizacja SeekBar i EditText
        val minPriceSeekBar: SeekBar = filterDialog.findViewById(R.id.minPriceSeekBar)
        val maxPriceSeekBar: SeekBar = filterDialog.findViewById(R.id.maxPriceSeekBar)
        val minPriceEditText: EditText = filterDialog.findViewById(R.id.editTextMinPrice)
        val maxPriceEditText: EditText = filterDialog.findViewById(R.id.editTextMaxPrice)

        // Ustawienie wartości początkowych
        minPriceSeekBar.progress = 0
        maxPriceSeekBar.progress = 5000

        // Obsługa zmiany wartości w SeekBar dla ceny minimalnej
        minPriceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                minPriceEditText.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Obsługa zmiany wartości w SeekBar dla ceny maksymalnej
        maxPriceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                maxPriceEditText.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        filterDialog.findViewById<Button>(R.id.applyFiltersButton).setOnClickListener {
            applyFilters(
                categories = categoryCheckboxes.filter { it.first.isChecked }.map { it.second },
                brands = brandCheckboxes.filter { it.first.isChecked }.map { it.second },
                minPrice = minPriceEditText.text.toString().toIntOrNull(),
                maxPrice = maxPriceEditText.text.toString().toIntOrNull()
            )
            filterDialog.dismiss()
            performSearch(searchEditText.text.toString()) // Zastosuj wyszukiwanie po filtrach
        }

        filterDialog.findViewById<Button>(R.id.clearFiltersButton).setOnClickListener {
            categoryCheckboxes.forEach { it.first.isChecked = false }
            brandCheckboxes.forEach { it.first.isChecked = false }
            filteredProducts = productsList
            minPriceSeekBar.progress = 0
            maxPriceSeekBar.progress = 5000
            minPriceEditText.setText("0")
            maxPriceEditText.setText("5000")
            filterDialog.dismiss()
            performSearch(searchEditText.text.toString())
        }

        filterDialog.show()
    }

    private fun filterProductsByQuery(query: String): List<Product> {
        return if (query.isEmpty()) {
            filteredProducts
        } else {
            filteredProducts.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.brand.contains(query, ignoreCase = true)
            }
        }
    }

    private fun sortProductsByName() {
        val query = searchEditText.text.toString()
        filteredProducts = filterProductsByQuery(query).sortedBy { it.name }
        searchAdapter.updateData(filteredProducts)
    }

    private fun sortProductsByPriceAscending() {
        val query = searchEditText.text.toString()
        filteredProducts = filterProductsByQuery(query).sortedBy { it.price }
        searchAdapter.updateData(filteredProducts)
    }

    private fun sortProductsByPriceDescending() {
        val query = searchEditText.text.toString()
        filteredProducts = filterProductsByQuery(query).sortedByDescending { it.price }
        searchAdapter.updateData(filteredProducts)
    }

    private fun applyFilters(categories: List<String>, brands: List<String>, minPrice: Int?, maxPrice: Int?) {
        var result = productsList

        if (categories.isNotEmpty()) {
            result = result.filter { it.category in categories }
        }

        if (brands.isNotEmpty()) {
            result = result.filter { it.brand in brands }
        }

        if (minPrice != null || maxPrice != null) {
            result = result.filter {
                val price = it.price
                val isAboveMin = minPrice?.let { min -> price >= min } ?: true
                val isBelowMax = maxPrice?.let { max -> price <= max } ?: true
                isAboveMin && isBelowMax
            }
        }

        filteredProducts = result
    }
}
