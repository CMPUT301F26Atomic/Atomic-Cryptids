package com.example.myapplication

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

/**
 * Draws lottery winners once a lottery's registration period has ended.
 * The draw runs lazily whenever [runDraws] is called and is protected by a
 * Firestore transaction on drawCompleted, so concurrent devices can never
 * draw the same lottery twice.
 */
object LotteryDraw {

    fun pickWinners(entrants: List<String>, numberOfWinners: Int, random: Random = Random.Default): List<String> =
        if (entrants.size <= numberOfWinners) entrants
        else entrants.shuffled(random).take(numberOfWinners)

    fun isDrawDue(nowMillis: Long, registrationEnd: Long?, drawCompleted: Boolean): Boolean =
        !drawCompleted && registrationEnd != null && nowMillis >= registrationEnd

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
                        transaction.update(
                            doc.reference, mapOf(
                                "winners" to pickWinners(pool, winnersCount),
                                "drawCompleted" to true,
                                "drawnAt" to System.currentTimeMillis(),
                            )
                        )
                        null
                    }.addOnCompleteListener {
                        remaining--
                        if (remaining == 0) onComplete("Draws completed")
                    }
                }
            }
            .addOnFailureListener { onComplete("Error: ${it.message}") }
    }
}
