package com.example.fit

import com.example.fit.data.local.WorkoutCatalog
import com.example.fit.data.model.Completed
import com.example.fit.data.model.StoredIds
import com.example.fit.utils.kazakhLabel
import com.example.fit.utils.kazakhTitle
import org.junit.Assert.*
import org.junit.Test

class KazakhLocalizationTest {
    @Test fun stableAndLegacyValuesNormalizeToSameIds() {
        assertEquals("weight_loss",StoredIds.requireGoal("Похудение"))
        assertEquals("weight_loss",StoredIds.requireGoal("Салмақ тастау"))
        assertEquals("maintenance",StoredIds.requireGoal("maintenance"))
        assertEquals("full_body",StoredIds.requireCategory("Всё тело"))
        assertEquals("chest",StoredIds.requireCategory("Кеуде"))
        assertEquals("Арқа",kazakhLabel("back"))
        assertEquals("Төзімділікті дамыту",kazakhLabel("endurance"))
    }
    @Test fun unknownStoredIdsAreRejectedForWrites() {
        assertNull(StoredIds.goal("unknown"))
        assertNull(StoredIds.category("unknown"))
        assertThrows(IllegalArgumentException::class.java) {StoredIds.requireGoal("unknown")}
        assertThrows(IllegalArgumentException::class.java) {StoredIds.requireCategory("unknown")}
        assertEquals("Белгісіз мән",kazakhLabel("unknown"))
    }

    @Test fun existingCategoryValuesStayCompatibleWithFirestore() {
        assertEquals(StoredIds.categories.toSet(),WorkoutCatalog.all.map {it.category}.toSet())
        WorkoutCatalog.all.forEach { assertNotEquals(it.category,kazakhLabel(it.category)) }
        assertEquals("Бүкіл дене",kazakhLabel("Всё тело"))
    }
    @Test fun existingGoalsHaveKazakhLabelsWithoutChangingStoredValues() {
        val goals=listOf("Похудение","Набор мышечной массы","Поддержание формы","Развитие выносливости")
        assertEquals(listOf("Салмақ тастау","Бұлшықет массасын арттыру","Дене бітімін сақтау","Төзімділікті дамыту"),goals.map(::kazakhLabel))
    }
    @Test fun oldHistoryIsPresentedInKazakhWithoutMutatingRecord() {
        val record=Completed("existing-session","full","Всё тело • старт","Всё тело",25.0,1234L)
        assertEquals("Бүкіл дене • алғашқы қадам",kazakhTitle(record))
        assertEquals("Всё тело • старт",record.title)
        assertEquals("existing-session",record.id)
        assertEquals(1234L,record.completedAt)
    }
    @Test fun catalogKeepsAllExistingWorkoutIds() {
        assertEquals(listOf("chest-start","chest-power","back-health","back-strength","legs-base","legs-power","arms","shoulders","abs","cardio","full","full-power"),WorkoutCatalog.all.map{it.id})
        assertTrue(WorkoutCatalog.all.all {it.level in listOf("Бастапқы деңгей","Орта деңгей")})
    }
}
