package com.example.creativecommissionstracker.data

import com.google.firebase.firestore.FirebaseFirestore
import com.example.creativecommissionstracker.model.*
import com.google.firebase.firestore.toObjects
import com.example.creativecommissionstracker.model.Order

class FirebaseRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // HOME SCREEN

    fun getFeaturedArtworks(limit: Int = 5, onResult: (List<Artwork>) -> Unit) {
        db.collection("artworks")
            .get()
            .addOnSuccessListener { snapshot ->
                val all = snapshot.toObjects<Artwork>()
                onResult(all.shuffled().take(limit))
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getFeaturedArtists(limit: Int = 3, onResult: (List<Artist>) -> Unit) {
        db.collection("artists")
            .get()
            .addOnSuccessListener { snapshot ->
                val all = snapshot.toObjects<Artist>()
                onResult(all.shuffled().take(limit))
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // ORDERS

    fun getOrders(onResult: (List<Order>) -> Unit) {
        db.collection("orders")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val order = doc.toObject(Order::class.java)
                    order?.copy(id = doc.id)
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun addOrUpdateOrder(order: Order, onDone: () -> Unit) {
        val docRef = if (order.id.isEmpty()) {
            db.collection("orders").document() // new
        } else {
            db.collection("orders").document(order.id) // update
        }

        val data = order.copy(id = docRef.id)

        docRef.set(data)
            .addOnSuccessListener { onDone() }
            .addOnFailureListener { onDone() }
    }

    fun getOrderById(orderId: String, onResult: (Order?) -> Unit) {
        db.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { doc ->
                val order = doc.toObject(Order::class.java)
                    ?.copy(id = doc.id)
                onResult(order)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


    // CLIENTS

    fun getClients(onResult: (List<Client>) -> Unit) {
        db.collection("clients")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val client = doc.toObject(Client::class.java)
                    client?.copy(id = doc.id)
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun addClient(client: Client, onDone: () -> Unit) {
        val docRef = db.collection("clients").document()
        val data = client.copy(id = docRef.id)

        docRef.set(data)
            .addOnSuccessListener { onDone() }
            .addOnFailureListener { onDone() }
    }
}