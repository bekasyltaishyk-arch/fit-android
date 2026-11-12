package com.example.fit.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fit.R
import com.example.fit.ui.components.*
import com.example.fit.ui.theme.*

@Composable fun SettingsScreen(pounds: Boolean, busy: Boolean, onUnit: (Boolean)->Unit, onLogout: ()->Unit) {
    Page(stringResource(R.string.fit_text_082),subtitle=stringResource(R.string.settings_subtitle)) {
        FitPanel {
            FitBrand();Text("1.0",style=MaterialTheme.typography.labelLarge,color=FitMuted)
            Text(stringResource(R.string.fit_text_083),style=MaterialTheme.typography.bodyMedium,color=FitMuted)
        }
        FitSectionHeader(stringResource(R.string.fit_text_084))
        FitPanel {
            listOf(false to stringResource(R.string.fit_text_085),true to stringResource(R.string.fit_text_086)).forEach { (value,label) ->
                Surface(onClick={onUnit(value)},shape=RoundedCornerShape(14.dp),color=if(pounds==value) Lime.copy(alpha=.1f) else FitSurface,border=BorderStroke(1.dp,if(pounds==value) Lime else MaterialTheme.colorScheme.outlineVariant)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        Icon(if(pounds==value) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,null,tint=if(pounds==value) Lime else FitMuted)
                        Text(label,style=MaterialTheme.typography.titleMedium)
                    }
                }
            }
            Text(stringResource(R.string.fit_text_087),style=MaterialTheme.typography.bodySmall,color=FitMuted)
        }
        FitCard(stringResource(R.string.appearance_title),stringResource(R.string.fit_text_088))
        FitSectionHeader(stringResource(R.string.account_title))
        OutlinedButton(onLogout,Modifier.fillMaxWidth().heightIn(min=52.dp),enabled=!busy,shape=RoundedCornerShape(16.dp),border=BorderStroke(1.dp,MaterialTheme.colorScheme.error.copy(alpha=.4f))) {Icon(Icons.AutoMirrored.Filled.Logout,null,Modifier.size(18.dp),tint=MaterialTheme.colorScheme.error);Spacer(Modifier.width(8.dp));Text(stringResource(R.string.fit_text_089),color=MaterialTheme.colorScheme.error)}
    }
}
@Composable fun SuccessScreen(onProgress: ()->Unit,onHome: ()->Unit) {
    Page(stringResource(R.string.fit_text_078),subtitle=stringResource(R.string.success_subtitle),showBrand=true) {
        FitPanel {
            Surface(Modifier.size(72.dp).align(Alignment.CenterHorizontally),shape=CircleShape,color=Lime.copy(alpha=.13f)) {Box(contentAlignment=Alignment.Center) {Icon(Icons.Default.Check,null,Modifier.size(38.dp),tint=Lime)}}
            Text(stringResource(R.string.fit_text_079),style=MaterialTheme.typography.bodyLarge)
            FitButton(stringResource(R.string.fit_text_080),onProgress,icon=Icons.Default.BarChart)
            OutlinedButton(onHome,Modifier.fillMaxWidth().heightIn(min=50.dp),shape=RoundedCornerShape(16.dp)) {Text(stringResource(R.string.fit_text_081))}
        }
    }
}

