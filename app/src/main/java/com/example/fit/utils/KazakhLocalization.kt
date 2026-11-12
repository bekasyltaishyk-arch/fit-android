package com.example.fit.utils

import com.example.fit.data.local.WorkoutCatalog
import com.example.fit.data.model.Completed
import com.example.fit.data.model.StoredIds

/** Kazakh presentation for both stable IDs and legacy values. */
fun kazakhLabel(value: String): String = when(StoredIds.category(value) ?: StoredIds.goal(value) ?: value) {
    "chest" -> "Кеуде"
    "back" -> "Арқа"
    "legs" -> "Аяқ"
    "arms" -> "Қол"
    "shoulders" -> "Иық"
    "abs" -> "Іш бұлшықеттері"
    "cardio" -> "Жүрек-қантамыр жаттығулары"
    "full_body" -> "Бүкіл дене"
    "weight_loss" -> "Салмақ тастау"
    "muscle_gain" -> "Бұлшықет массасын арттыру"
    "maintenance" -> "Дене бітімін сақтау"
    "endurance" -> "Төзімділікті дамыту"
    "Начальный" -> "Бастапқы деңгей"
    "Средний" -> "Орта деңгей"
    "Барлығы", "Бастапқы деңгей", "Орта деңгей" -> value
    else -> "Белгісіз мән"
}
fun kazakhTitle(record: Completed): String = WorkoutCatalog.find(record.workoutId)?.title ?: "Жаттығу"
