package com.example.fit.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fit.data.local.WorkoutCatalog
import com.example.fit.data.model.Profile
import com.example.fit.ui.theme.FitTheme
import com.example.fit.ui.viewmodel.FitState

@Preview(name="FIT — 360 dp",widthDp=360,heightDp=800,showBackground=true)
@Preview(name="FIT — small screen",widthDp=320,heightDp=720,showBackground=true)
@Composable private fun CatalogPreview() {FitTheme {Surface(Modifier.fillMaxSize()) {Catalog {}}}}

@Preview(name="FIT — large Kazakh text",widthDp=360,heightDp=800,fontScale=1.3f,showBackground=true)
@Composable private fun DetailLargeTextPreview() {FitTheme {Surface(Modifier.fillMaxSize()) {Detail(WorkoutCatalog.all.first(),true) {}}}}

@Preview(name="FIT — empty dashboard",widthDp=360,heightDp=800,showBackground=true)
@Composable private fun HomePreview() {FitTheme {Surface(Modifier.fillMaxSize()) {Home(FitState(loading=false,profile=Profile(name="Айдана")),{}, {})}}}
