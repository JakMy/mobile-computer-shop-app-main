package com.example.myshopapp.`object`

import Product
import com.example.myshopapp.data.*

object PCConfigurator {
    private var case: Case? = null
    private var cooling: Cooling? = null
    private var graphicCard: GraphicCard? = null
    private var memory: Memory? = null
    private var motherboard: Motherboard? = null
    private var powerSupply: PowerSupply? = null
    private var processor: Processor? = null
    private var ssd: SSD? = null

    fun getCase() = case
    fun getCooling() = cooling
    fun getGraphicCard() = graphicCard
    fun getMemory() = memory
    fun getMotherboard() = motherboard
    fun getPowerSupply() = powerSupply
    fun getProcessor() = processor
    fun getSSD() = ssd

    private var listeners = mutableListOf<ConfiguratorChangeListener>()

    interface ConfiguratorChangeListener {
        fun onConfiguratorChanged(message: String)
    }

    fun addListener(listener: ConfiguratorChangeListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ConfiguratorChangeListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        val message = checkCompatibility()
        listeners.forEach { it.onConfiguratorChanged(message) }
    }

    fun checkCompatibility(): String {
        val messages = mutableListOf<String>()

        val selectedCase = getCase()
        val selectedGraphicCard = getGraphicCard()
        val selectedCooling = getCooling()
        val selectedPowerSupply = getPowerSupply()
        val selectedMemory = getMemory()
        val selectedMotherboard = getMotherboard()
        val selectedProcessor = getProcessor()

        // Sprawdzenie kompatybilności wielkości karty graficznej z obudową
        if (selectedCase != null && selectedGraphicCard != null) {
            if (selectedCase.max_size_of_graphic_card!! < selectedGraphicCard.size!!) {
                messages.add("The selected graphic card does not fit in the case.")
            }
        }

        // Sprawdzenie kompatybilności wielkości chłodzenia z obudową
        if (selectedCase != null && selectedCooling != null) {
            if (selectedCase.max_size_of_cooling!! < selectedCooling.radiator_size!!) {
                messages.add("The selected cooling system does not fit in the case.")
            }
        }

        // Sprawdzenie kompatybilności zasilacza z obudową
        if (selectedCase != null && selectedCase.power_supply != "0" && getPowerSupply() != null) {
            messages.add("The case already has a built-in power supply.")
        }

        // Sprawdzenie, czy moc zasilacza jest odpowiednia dla karty graficznej
        if (selectedGraphicCard != null && selectedPowerSupply != null) {
            val recommendedWattage = selectedGraphicCard.recommendedWattage?.toIntOrNull()
            val powerSupplyWattage = selectedPowerSupply.wattage?.toIntOrNull()

            if (recommendedWattage != null && powerSupplyWattage != null) {
                if (powerSupplyWattage < recommendedWattage) {
                    messages.add("The power supply wattage is too low for the selected graphic card.")
                }
            }
        }

        // Sprawdzenie kompatybilności wbudowanego zasilacza z wymaganiami karty graficznej
        if (selectedCase != null && selectedCase.power_supply != "0" && selectedGraphicCard != null) {
            val builtInPowerSupplyWattage = selectedCase.power_supply?.toIntOrNull()
            val recommendedWattage = selectedGraphicCard.recommendedWattage?.toIntOrNull()

            if (builtInPowerSupplyWattage != null && recommendedWattage != null) {
                if (builtInPowerSupplyWattage < recommendedWattage) {
                    messages.add("The built-in power supply in the case is too weak for the selected graphic card.")
                }
            }
        }

        // Sprawdzenie kompatybilności standardu pamięci RAM z płytą główną
        if (selectedMemory != null && selectedMotherboard != null) {
            if (selectedMemory.type != selectedMotherboard.memory_standard) {
                messages.add("The selected RAM memory and motherboard use incompatible memory standards.")
            }
        }

        // Sprawdzenie kompatybilności socketów procesora i płyty głównej
        if (selectedProcessor != null && selectedMotherboard != null) {
            if (selectedProcessor.socket != selectedMotherboard.socket) {
                messages.add("The selected processor and motherboard have incompatible sockets.")
            }
        }

        // Jeśli nie ma wiadomości, zwróć wiadomość, że wszystko jest kompatybilne
        if (messages.isEmpty()) {
            messages.add("All selected components are compatible.")
        }

        return messages.joinToString("\n")
    }






    fun addProduct(product: Product): Boolean {
        return when (product) {
            is Case -> {
                case = product
                true
            }
            is Cooling -> {
                cooling = product
                true
            }
            is GraphicCard -> {
                graphicCard = product
                true
            }
            is Memory -> {
                memory = product
                true
            }
            is Motherboard -> {
                motherboard = product
                true
            }
            is PowerSupply -> {
                powerSupply = product
                true
            }
            is Processor -> {
                processor = product
                true
            }
            is SSD -> {
                ssd = product
                true
            }
            else -> false
        }
    }

    fun removeProduct(product: Product) {
        when (product) {
            is Case -> case = null
            is Cooling -> cooling = null
            is GraphicCard -> graphicCard = null
            is Memory -> memory = null
            is Motherboard -> motherboard = null
            is PowerSupply -> powerSupply = null
            is Processor -> processor = null
            is SSD -> ssd = null
        }
    }

    fun clear() {
        case = null
        cooling = null
        graphicCard = null
        memory = null
        motherboard = null
        powerSupply = null
        processor = null
        ssd = null
        notifyListeners()
    }

    fun getTotalPrice(): Double {
        return listOfNotNull(
            case?.price,
            cooling?.price,
            graphicCard?.price,
            memory?.price,
            motherboard?.price,
            powerSupply?.price,
            processor?.price,
            ssd?.price
        ).sum()
    }

    fun removeCase() {
        case = null
        notifyListeners()
    }

    fun removeMemory() {
        memory = null
        notifyListeners()
    }

    fun removeSSD() {
        ssd = null
        notifyListeners()
    }

    fun removePowerSupply() {
        powerSupply = null
        notifyListeners()
    }

    fun removeMotherboard() {
        motherboard = null
        notifyListeners()
    }

    fun removeProcessor() {
        processor = null
        notifyListeners()
    }

    fun removeGraphicCard() {
        graphicCard = null
        notifyListeners()
    }

    fun removeCooling() {
        cooling = null
        notifyListeners()
    }

    fun addToCart() {
        case?.let { Cart.addProduct(it) }
        cooling?.let { Cart.addProduct(it) }
        graphicCard?.let { Cart.addProduct(it) }
        memory?.let { Cart.addProduct(it) }
        motherboard?.let { Cart.addProduct(it) }
        powerSupply?.let { Cart.addProduct(it) }
        processor?.let { Cart.addProduct(it) }
        ssd?.let { Cart.addProduct(it) }
    }
}
