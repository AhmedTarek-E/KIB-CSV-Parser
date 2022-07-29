package com.ahmed.scratching.csvreader

import java.io.File

data class Order(
    val id: String,
    val area: String,
    val productName: String,
    val quantity: Int,
    val brand: String
)

data class DistinctOrder(
    val productName: String,
    var totalQuantity: Int,
    val brands: HashMap<String, Int>
) {
    fun mostPopularBrand(): String {
        var mostPopular: MutableMap.MutableEntry<String, Int>? = null
        for (brand in brands) {
            if (brand.value > (mostPopular?.value ?: 0)) {
                mostPopular = brand
            }
        }
        return mostPopular?.key ?: ""
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("No Input file is provided")
        return
    }

    val filePath = args.first().split("/").dropLast(1).joinToString("/")
    val inputName = args.first().split("/").last()
    val bufferedReader = File(args.first()).bufferedReader()

    val orders = arrayListOf<Order>()
    bufferedReader.forEachLine {
        val splitted = it.split(",")
        if (splitted.size == 5) {
            orders.add(
                Order(
                    splitted[0],
                    splitted[1],
                    splitted[2],
                    splitted[3].toInt(),
                    splitted[4]
                )
            )
        }
    }

    val distinctOrders = hashMapOf<String, DistinctOrder>()
    for (order in orders) {
        val item = distinctOrders[order.productName]
        if (item == null) {
            distinctOrders[order.productName] = DistinctOrder(
                order.productName,
                order.quantity,
                hashMapOf(order.brand to 1)
            )
        } else {
            item.totalQuantity += order.quantity
            val brandNumber = item.brands[order.brand]
            if (brandNumber == null) {
                item.brands[order.brand] = 1
            } else {
                item.brands[order.brand] = brandNumber + 1
            }
        }
    }

    val firstContent = distinctOrders.values.joinToString("\n") {
        "${it.productName},${it.totalQuantity.toDouble()/orders.size}"
    }

    val secondContent = distinctOrders.values.joinToString("\n") {
        "${it.productName},${it.mostPopularBrand()}"
    }

    println("first:\n$firstContent")
    println("second:\n$secondContent")

    File("$filePath/0_$inputName").writeText(firstContent)
    File("$filePath/1_$inputName").writeText(secondContent)

}