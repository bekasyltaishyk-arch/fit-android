package com.example.fit

import com.example.fit.data.local.WorkoutCatalog
import com.example.fit.data.model.StoredIds
import com.example.fit.ui.components.categoryArtwork
import org.junit.Assert.*
import org.junit.Test

class WorkoutArtworkTest {
    @Test fun eachCategoryHasDistinctLocalArtwork() {
        val resources=StoredIds.categories.map(::categoryArtwork)
        assertEquals(8,resources.distinct().size)
        resources.forEach {assertNotEquals(R.drawable.workout_fallback,it)}
        assertTrue(WorkoutCatalog.all.all {categoryArtwork(it.category) in resources})
    }
    @Test fun unknownCategoryUsesFallback() {
        assertEquals(R.drawable.workout_fallback,categoryArtwork("unknown"))
    }
}
