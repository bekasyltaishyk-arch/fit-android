package com.example.fit.data.model

import java.time.*

data class Exercise(val name: String, val sets: Int, val reps: String, val instruction: String)
data class Workout(val id: String, val title: String, val category: String, val minutes: Int, val level: String, val description: String, val exercises: List<Exercise>)
data class Profile(val name: String = "", val email: String = "", val age: Int = 0, val height: Double = 0.0, val weight: Double = 0.0, val goal: String = StoredIds.DEFAULT_GOAL, val createdAt: Long = 0)
data class Completed(val id: String, val workoutId: String, val title: String, val category: String, val durationMinutes: Double, val completedAt: Long)
data class Weight(val id: String, val weight: Double, val recordedAt: Long)
data class Stats(val total: Int, val minutes: Double, val week: Int, val month: Int, val days: List<Int>)
fun statistics(records: List<Completed>, today: LocalDate = LocalDate.now(), zone: ZoneId = ZoneId.systemDefault()): Stats {
    fun date(r: Completed) = Instant.ofEpochMilli(r.completedAt).atZone(zone).toLocalDate()
    val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    return Stats(records.size, records.sumOf { it.durationMinutes }, records.count { date(it) in monday..today }, records.count { date(it).year == today.year && date(it).month == today.month }, (6 downTo 0).map { offset -> records.count { date(it) == today.minusDays(offset.toLong()) } })
}
