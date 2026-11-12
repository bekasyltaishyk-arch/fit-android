package com.example.fit.data.model

/** Stable stored IDs; aliases are accepted on reads and normalized before writes. */
object StoredIds {
    val goals = listOf("weight_loss", "muscle_gain", "maintenance", "endurance")
    val categories = listOf("chest", "back", "legs", "arms", "shoulders", "abs", "cardio", "full_body")
    const val DEFAULT_GOAL = "maintenance"
    private val goalAliases = mapOf(
        "Похудение" to "weight_loss", "Салмақ тастау" to "weight_loss",
        "Набор мышечной массы" to "muscle_gain", "Бұлшықет массасын арттыру" to "muscle_gain",
        "Поддержание формы" to "maintenance", "Дене бітімін сақтау" to "maintenance",
        "Развитие выносливости" to "endurance", "Төзімділікті дамыту" to "endurance"
    )
    private val categoryAliases = mapOf(
        "Грудь" to "chest", "Кеуде" to "chest", "Спина" to "back", "Арқа" to "back",
        "Ноги" to "legs", "Аяқ" to "legs", "Руки" to "arms", "Қол" to "arms",
        "Плечи" to "shoulders", "Иық" to "shoulders", "Пресс" to "abs", "Іш бұлшықеттері" to "abs",
        "Кардио" to "cardio", "Жүрек-қантамыр жаттығулары" to "cardio",
        "Всё тело" to "full_body", "Бүкіл дене" to "full_body"
    )
    fun goal(value: String): String? = value.takeIf { it in goals } ?: goalAliases[value]
    fun category(value: String): String? = value.takeIf { it in categories } ?: categoryAliases[value]
    fun requireGoal(value: String): String = requireNotNull(goal(value)) { "Фитнес мақсатын таңдаңыз" }
    fun requireCategory(value: String): String = requireNotNull(category(value)) { "Жаттығу санаты белгісіз" }
}
