package com.example.myapplication

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

/**
 * Draws lottery winners once a lottery's registration period has ended.
 * The draw runs lazily whenever [runDraws] is called. A firestore transatcion 
 * on drawCompleted keeps the process atomic, so concurrent devices can never
 * draw the same lottery multiple times.
 *
 * TODO: Need to confirm whether both waitlist and littery list have same weight
 * for drawing lottery or we draw from only one or both but with a probability weight.
 */
object LotteryDraw {

    fun pickWinners(entrants: List<String>, numberOfWinners: Int, random: Random = Random.Default): List<String> =
        if (entrants.size <= numberOfWinners) entrants
        else entrants.shuffled(random).take(numberOfWinners)

    fun isDrawDue(nowMillis: Long, registrationEnd: Long?, drawCompleted: Boolean): Boolean =
        !drawCompleted && registrationEnd != null && nowMillis >= registrationEnd

    fun getCampsiteNames(campsiteIds: List<String>, onResult: (String) -> Unit) {
        if (campsiteIds.isEmpty()) {
            onResult("a campsite")
            return
        }

        val db = FirebaseFirestore.getInstance()
        val campsiteNames = mutableListOf<String>()
        var remaining = campsiteIds.size

        for (campsiteId in campsiteIds) {
            db.collection("campsites").document(campsiteId)
                .get()
                .addOnSuccessListener { campsiteDoc ->
                    val name = campsiteDoc.getString("name")
                    if (name != null) {
                        campsiteNames.add(name)
                    }
                }
                .addOnCompleteListener {
                    remaining -= 1
                    if (remaining == 0) {
                        onResult(campsiteNames.joinToString(", "))
                    }
                }
        }
    }

    fun runDraws(onComplete: (String) -> Unit = {}) {
        val db = FirebaseFirestore.getInstance()
        db.collection("lotteries")
            .whereEqualTo("drawCompleted", false)
            .get()
            .addOnSuccessListener { snap ->
                val due = snap.documents.filter {
                    isDrawDue(System.currentTimeMillis(), it.getLong("registrationEndDate"), false)
                }
                if (due.isEmpty()) {
                    onComplete("No draws due")
                    return@addOnSuccessListener
                }
                var remaining = due.size
                due.forEach { doc ->

                    var lotteryPool = listOf<String>()
                    var lotteryWinners = listOf<String>()

                    db.runTransaction { transaction ->
                        val fresh = transaction.get(doc.reference)
                        if (fresh.getBoolean("drawCompleted") != false) {
                            throw IllegalStateException("This lottery has already been drawn.")
                        }
                        if (!isDrawDue(System.currentTimeMillis(), fresh.getLong("registrationEndDate"), false)) {
                            throw IllegalStateException("Draw is not due yet.")
                        }
                        val entrants = (fresh.get("entrants") as? List<*>)
                            .orEmpty().filterIsInstance<String>()
                        val waitlist = (fresh.get("waitlist") as? List<*>)
                            .orEmpty().filterIsInstance<String>()
                        val pool = (entrants + waitlist).distinct()
                        val winnersCount = (fresh.getLong("numberOfWinners") ?: 0L).toInt()

                        val winners = pickWinners(pool, winnersCount)
                        lotteryPool = pool
                        lotteryWinners = winners

                        transaction.update(
                            doc.reference, mapOf(
                                "winners" to winners,
                                "drawCompleted" to true,
                                "drawnAt" to System.currentTimeMillis(),
                            )
                        )
                        null

                    }.addOnSuccessListener {
                        val campsiteIds = (doc.get("campsiteIds") as? List<*>).orEmpty().filterIsInstance<String>()
                        getCampsiteNames(campsiteIds) { campsiteNames ->
                            for (uid in lotteryPool) {
                                if (uid in lotteryWinners) {
                                    Notification.send(uid, "win", "You won the lottery for $campsiteNames! You can now reserve your campsite.")
                                } else {
                                    Notification.send(uid, "lose", "You were not selected in the lottery for $campsiteNames.")
                                }
                            }
                        }

                    }.addOnCompleteListener {
                        remaining--
                        if (remaining == 0) onComplete("Draws completed")
                    }
                }
            }
            .addOnFailureListener { onComplete("Error: ${it.message}") }
    }
}
