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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myshopapp.CheckoutActivity
import com.example.myshopapp.R
import com.example.myshopapp.`object`.PCConfigurator

class ConfiguratorFragment : Fragment(), PCConfigurator.ConfiguratorChangeListener {

    private lateinit var caseProductName: TextView
    private lateinit var removeCaseButton: ImageButton
    private lateinit var memoryProductName: TextView
    private lateinit var removeMemoryButton: ImageButton
    private lateinit var ssdProductName: TextView
    private lateinit var removeSsdButton: ImageButton
    private lateinit var powerSupplyProductName: TextView
    private lateinit var removePowerSupplyButton: ImageButton
    private lateinit var motherboardProductName: TextView
    private lateinit var removeMotherboardButton: ImageButton
    private lateinit var processorProductName: TextView
    private lateinit var removeProcessorButton: ImageButton
    private lateinit var graphicCardProductName: TextView
    private lateinit var removeGraphicCardButton: ImageButton
    private lateinit var coolingProductName: TextView
    private lateinit var removeCoolingButton: ImageButton

    private lateinit var configuratorMessage: TextView
    private lateinit var configuratorTotalPrice: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configurator, container, false)

        // Inicjalizacja widoków
        caseProductName = view.findViewById(R.id.caseProductName)
        removeCaseButton = view.findViewById(R.id.removeCaseButton)
        memoryProductName = view.findViewById(R.id.memoryProductName)
        removeMemoryButton = view.findViewById(R.id.removeMemoryButton)
        ssdProductName = view.findViewById(R.id.ssdProductName)
        removeSsdButton = view.findViewById(R.id.removeSsdButton)
        powerSupplyProductName = view.findViewById(R.id.powerSupplyProductName)
        removePowerSupplyButton = view.findViewById(R.id.removePowerSupplyButton)
        motherboardProductName = view.findViewById(R.id.motherboardProductName)
        removeMotherboardButton = view.findViewById(R.id.removeMotherboardButton)
        processorProductName = view.findViewById(R.id.processorProductName)
        removeProcessorButton = view.findViewById(R.id.removeProcessorButton)
        graphicCardProductName = view.findViewById(R.id.graphicCardProductName)
        removeGraphicCardButton = view.findViewById(R.id.removeGraphicCardButton)
        coolingProductName = view.findViewById(R.id.coolingProductName)
        removeCoolingButton = view.findViewById(R.id.removeCoolingButton)

        configuratorMessage = view.findViewById(R.id.configuratorMessage)
        configuratorTotalPrice = view.findViewById(R.id.configuratorTotalPrice)

        // Rejestracja słuchacza zmian
        PCConfigurator.addListener(this)

        // Ustawienie wartości początkowych dla każdego komponentu
        updateUI()

        val caseProduct = PCConfigurator.getCase()
        if (caseProduct != null) {
            caseProductName.text = caseProduct.name
            removeCaseButton.visibility = View.VISIBLE
        } else {
            caseProductName.text = "No product selected"
            removeCaseButton.visibility = View.GONE
        }

        removeCaseButton.setOnClickListener {
            PCConfigurator.removeCase()
            updateConfiguratorMessage()
            caseProductName.text = "No product selected"
            removeCaseButton.visibility = View.GONE
        }

        val memoryProduct = PCConfigurator.getMemory()
        if (memoryProduct != null) {
            memoryProductName.text = memoryProduct.name
            removeMemoryButton.visibility = View.VISIBLE
        } else {
            memoryProductName.text = "No product selected"
            removeMemoryButton.visibility = View.GONE
        }

        removeMemoryButton.setOnClickListener {
            PCConfigurator.removeMemory()
            updateConfiguratorMessage()
            memoryProductName.text = "No product selected"
            removeMemoryButton.visibility = View.GONE
        }

        val ssdProduct = PCConfigurator.getSSD()
        if (ssdProduct != null) {
            ssdProductName.text = ssdProduct.name
            removeSsdButton.visibility = View.VISIBLE
        } else {
            ssdProductName.text = "No product selected"
            removeSsdButton.visibility = View.GONE
        }

        removeSsdButton.setOnClickListener {
            PCConfigurator.removeSSD()
            updateConfiguratorMessage()
            ssdProductName.text = "No product selected"
            removeSsdButton.visibility = View.GONE
        }

        val powerSupplyProduct = PCConfigurator.getPowerSupply()
        if (powerSupplyProduct != null) {
            powerSupplyProductName.text = powerSupplyProduct.name
            removePowerSupplyButton.visibility = View.VISIBLE
        } else {
            powerSupplyProductName.text = "No product selected"
            removePowerSupplyButton.visibility = View.GONE
        }

        removePowerSupplyButton.setOnClickListener {
            PCConfigurator.removePowerSupply()
            updateConfiguratorMessage()
            powerSupplyProductName.text = "No product selected"
            removePowerSupplyButton.visibility = View.GONE
        }

        val motherboardProduct = PCConfigurator.getMotherboard()
        if (motherboardProduct != null) {
            motherboardProductName.text = motherboardProduct.name
            removeMotherboardButton.visibility = View.VISIBLE
        } else {
            motherboardProductName.text = "No product selected"
            removeMotherboardButton.visibility = View.GONE
        }

        removeMotherboardButton.setOnClickListener {
            PCConfigurator.removeMotherboard()
            updateConfiguratorMessage()
            motherboardProductName.text = "No product selected"
            removeMotherboardButton.visibility = View.GONE
        }

        val processorProduct = PCConfigurator.getProcessor()
        if (processorProduct != null) {
            processorProductName.text = processorProduct.name
            removeProcessorButton.visibility = View.VISIBLE
        } else {
            processorProductName.text = "No product selected"
            removeProcessorButton.visibility = View.GONE
        }

        removeProcessorButton.setOnClickListener {
            PCConfigurator.removeProcessor()
            updateConfiguratorMessage()
            processorProductName.text = "No product selected"
            removeProcessorButton.visibility = View.GONE
        }

        val graphicCardProduct = PCConfigurator.getGraphicCard()
        if (graphicCardProduct != null) {
            graphicCardProductName.text = graphicCardProduct.name
            removeGraphicCardButton.visibility = View.VISIBLE
        } else {
            graphicCardProductName.text = "No product selected"
            removeGraphicCardButton.visibility = View.GONE
        }

        removeGraphicCardButton.setOnClickListener {
            PCConfigurator.removeGraphicCard()
            updateConfiguratorMessage()
            graphicCardProductName.text = "No product selected"
            removeGraphicCardButton.visibility = View.GONE
        }

        val coolingProduct = PCConfigurator.getCooling()
        if (coolingProduct != null) {
            coolingProductName.text = coolingProduct.name
            removeCoolingButton.visibility = View.VISIBLE
        } else {
            coolingProductName.text = "No product selected"
            removeCoolingButton.visibility = View.GONE
        }

        removeCoolingButton.setOnClickListener {
            PCConfigurator.removeCooling()
            updateConfiguratorMessage()
            coolingProductName.text = "No product selected"
            removeCoolingButton.visibility = View.GONE
        }

        // Ustawienie przycisków
        val addAllToCartButton = view.findViewById<Button>(R.id.addAllToCartButton)
        val buyAllButton = view.findViewById<Button>(R.id.buyAllNowButton)
        val clearConfiguratorButton = view.findViewById<Button>(R.id.clearConfiguratorButton)

        addAllToCartButton.setOnClickListener {
            PCConfigurator.addToCart()
            Toast.makeText(requireContext(), "All products added to cart", Toast.LENGTH_SHORT).show()
        }

        buyAllButton.setOnClickListener {
            PCConfigurator.addToCart()
            val intent = Intent(requireContext(), CheckoutActivity::class.java)
            startActivity(intent)
        }

        clearConfiguratorButton.setOnClickListener {
            PCConfigurator.clear()
            updateUI()
            updateConfiguratorMessage()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Usunięcie słuchacza, gdy widok zostanie zniszczony
        PCConfigurator.removeListener(this)
    }

    override fun onConfiguratorChanged(message: String) {
        // Aktualizacja TextView na podstawie zmian w konfiguratorze
        configuratorMessage.text = message
        updateTotalPrice()
    }

    private fun updateConfiguratorMessage() {
        configuratorMessage.text = PCConfigurator.checkCompatibility()
    }

    private fun updateUI() {
        val case = PCConfigurator.getCase()
        val memory = PCConfigurator.getMemory()
        val ssd = PCConfigurator.getSSD()
        val powerSupply = PCConfigurator.getPowerSupply()
        val motherboard = PCConfigurator.getMotherboard()
        val processor = PCConfigurator.getProcessor()
        val graphicCard = PCConfigurator.getGraphicCard()
        val cooling = PCConfigurator.getCooling()

        // Aktualizacja widoku dla każdej kategorii
        updateProductView(case, caseProductName, removeCaseButton)
        updateProductView(memory, memoryProductName, removeMemoryButton)
        updateProductView(ssd, ssdProductName, removeSsdButton)
        updateProductView(powerSupply, powerSupplyProductName, removePowerSupplyButton)
        updateProductView(motherboard, motherboardProductName, removeMotherboardButton)
        updateProductView(processor, processorProductName, removeProcessorButton)
        updateProductView(graphicCard, graphicCardProductName, removeGraphicCardButton)
        updateProductView(cooling, coolingProductName, removeCoolingButton)

        // Aktualizacja wiadomości o kompatybilności i cenie łącznej po każdej zmianie
        updateTotalPrice()
        updateConfiguratorMessage()
    }

    private fun updateProductView(product: Product?, productNameView: TextView, removeButton: ImageButton) {
        if (product != null) {
            productNameView.text = product.name
            removeButton.visibility = View.VISIBLE
            removeButton.setOnClickListener {
                PCConfigurator.removeProduct(product)
                updateUI()
            }
        } else {
            productNameView.text = "No product selected"
            removeButton.visibility = View.GONE
        }
    }

    private fun updateTotalPrice() {
        val totalPrice = PCConfigurator.getTotalPrice()
        configuratorTotalPrice.text = "Total Price: %.2f zł".format(totalPrice)
    }
}
