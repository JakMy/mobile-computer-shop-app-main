import java.io.Serializable

open class Product(
    var id: String = "",
    var name: String = "",
    var brand: String = "",
    var price: Double = 0.0,
    var sale: Double = 0.0,
    var image_urls: List<String>? = null,
    var category: String = ""
) : Serializable
