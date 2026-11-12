package com.example.fit.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.example.fit.R
import com.example.fit.data.model.Workout
import com.example.fit.ui.theme.*
import com.example.fit.utils.kazakhLabel

@Composable fun FitBrand(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(8.dp, 25.dp).clip(RoundedCornerShape(3.dp)).background(Lime))
        Text("FIT", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
    }
}
@Composable fun FitButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, busy: Boolean = false, icon: ImageVector? = null) {
    Button(onClick, modifier.fillMaxWidth().heightIn(min = 52.dp), enabled = enabled && !busy, shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(16.dp, 14.dp)) {
        if (busy) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        else { if (icon != null) { Icon(icon, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)) }; Text(text) }
    }
}
@Composable fun FitPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = FitSurface, border = BorderStroke(1.dp, Color(0xFF292D32))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}
@Composable fun FitCard(title: String, subtitle: String, action: (() -> Unit)? = null) {
    val body: @Composable ColumnScope.() -> Unit = {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = FitMuted)
    }
    if (action == null) FitPanel(content = body)
    else Card(onClick = action, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = FitSurface), border = BorderStroke(1.dp, Color(0xFF292D32))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = body)
    }
}
@Composable fun FitSectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
        if(action != null && onAction != null) TextButton(onAction) { Text(action, style = MaterialTheme.typography.labelMedium) }
    }
}
@Composable fun FitStatCard(value: String, label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(20.dp), color = FitSurface, border = BorderStroke(1.dp, Color(0xFF292D32))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, Modifier.size(21.dp), tint = Lime)
            Text(value, style = if(value.length>8) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = FitMuted)
        }
    }
}
@Composable fun FitEmptyState(title: String, description: String, icon: ImageVector = Icons.Default.FitnessCenter, action: String? = null, onAction: (() -> Unit)? = null) {
    FitPanel {
        Icon(icon, null, Modifier.size(36.dp).background(Lime.copy(alpha = .1f), CircleShape).padding(6.dp), tint = Lime)
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = FitMuted)
        if(action != null && onAction != null) FitButton(action, onAction)
    }
}
@Composable fun FitLoadingState() {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        FitBrand(); Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.fit_text_077), color = FitMuted)
        Spacer(Modifier.height(32.dp)); CircularProgressIndicator(Modifier.size(32.dp), strokeWidth = 3.dp)
    }
}
@DrawableRes fun categoryArtwork(category: String): Int = when(category) {
    "chest" -> R.drawable.workout_chest
    "back" -> R.drawable.workout_back
    "legs" -> R.drawable.workout_legs
    "arms" -> R.drawable.workout_arms
    "shoulders" -> R.drawable.workout_shoulders
    "abs" -> R.drawable.workout_abs
    "cardio" -> R.drawable.workout_cardio
    "full_body" -> R.drawable.workout_full_body
    else -> R.drawable.workout_fallback
}
@Composable fun WorkoutImage(category: String, modifier: Modifier = Modifier) {
    Image(painterResource(categoryArtwork(category)), kazakhLabel(category), modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)), contentScale = ContentScale.Crop)
}
@OptIn(ExperimentalLayoutApi::class)
@Composable fun WorkoutMeta(w: Workout) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FitBadge("${w.minutes} мин", Icons.Default.Schedule)
        FitBadge(w.level, Icons.Default.SignalCellularAlt)
        FitBadge("${w.exercises.size} жаттығу", Icons.Default.FitnessCenter)
    }
}
@Composable fun FitBadge(text: String, icon: ImageVector? = null) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF282E26)) {
        Row(Modifier.padding(8.dp, 5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            if(icon != null) Icon(icon, null, Modifier.size(13.dp), tint = Lime)
            Text(text, style = MaterialTheme.typography.labelSmall, color = Color(0xFFD7E6C5))
        }
    }
}
@Composable fun FitWorkoutCard(w: Workout, onClick: () -> Unit) {
    Card(onClick, Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = FitSurface), shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, Color(0xFF292D32))) {
        Box {
            Image(painterResource(categoryArtwork(w.category)), kazakhLabel(w.category), Modifier.fillMaxWidth().height(156.dp), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxWidth().height(156.dp).background(Brush.verticalGradient(listOf(Color.Transparent, FitSurface.copy(alpha = .5f)))))
            Box(Modifier.align(Alignment.BottomStart).padding(14.dp)) { FitBadge(kazakhLabel(w.category)) }
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(w.title, style = MaterialTheme.typography.titleLarge)
            WorkoutMeta(w)
            Text(w.description, color = FitMuted, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.details_action), color = Lime, style = MaterialTheme.typography.labelLarge)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(20.dp), tint = Lime)
            }
        }
    }
}
@Composable fun FitHero(w: Workout, onStart: () -> Unit) {
    Surface(shape = RoundedCornerShape(24.dp), color = FitSurface) {
        Box(Modifier.fillMaxWidth().heightIn(min = 300.dp)) {
            Image(painterResource(categoryArtwork(w.category)), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
            Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = .15f), FitBackground.copy(alpha = .97f)))))
            Column(Modifier.padding(20.dp).align(Alignment.BottomStart), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.hero_eyebrow), color = Lime, style = MaterialTheme.typography.labelMedium)
                Text(stringResource(R.string.fit_text_022), style = MaterialTheme.typography.headlineLarge)
                Text(stringResource(R.string.hero_subtitle), color = Color(0xFFD1D5D9), style = MaterialTheme.typography.bodyMedium)
                FitButton(stringResource(R.string.fit_text_029), onStart, icon = Icons.Default.PlayArrow)
            }
        }
    }
}
@Composable fun FitProgressIndicator(progress: Float) {
    val animated by animateFloatAsState(progress.coerceIn(0f,1f), tween(350), label = "workoutProgress")
    LinearProgressIndicator(progress = { animated }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), trackColor = Color(0xFF363C32))
}
@Composable fun Chart(values: List<Float>, line: Boolean = false, labels: List<String> = emptyList()) {
    val data = if(line) values.takeLast(30) else values
    val summary = data.joinToString { "%.1f".format(it) }
    Column(Modifier.fillMaxWidth().semantics { contentDescription = summary }) {
        Canvas(Modifier.fillMaxWidth().height(140.dp).padding(vertical = 8.dp)) {
            val max = if(line) (data.maxOrNull() ?: 0f)+1f else (data.maxOrNull() ?: 1f).coerceAtLeast(1f)
            val min = if(line) (data.minOrNull() ?: 0f)-1f else 0f
            val range = (max-min).coerceAtLeast(1f)
            repeat(4) { j -> val y = size.height*j/3; drawLine(Color(0xFF363A40), Offset(0f,y), Offset(size.width,y), 1f) }
            val step = size.width / data.size.coerceAtLeast(1)
            val points = data.mapIndexed { i,v -> Offset(step*(i+.5f), size.height*(1-(v-min)/range)) }
            if(line && points.isNotEmpty()) {
                val path = Path().apply { moveTo(points.first().x,points.first().y); points.drop(1).forEach { lineTo(it.x,it.y) } }
                val area = Path().apply { addPath(path); lineTo(points.last().x,size.height);lineTo(points.first().x,size.height);close() }
                drawPath(area, Brush.verticalGradient(listOf(Lime.copy(alpha = .22f),Color.Transparent)))
                drawPath(path,Lime,style = Stroke(3.dp.toPx(),cap = StrokeCap.Round,join = StrokeJoin.Round))
                points.forEach { drawCircle(Lime,3.dp.toPx(),it) }
            } else points.forEachIndexed { i,p ->
                if(data[i]>0) drawRoundRect(Brush.verticalGradient(listOf(Lime,Lime.copy(alpha = .4f))),Offset(p.x-step*.25f,p.y),Size(step*.5f,(size.height-p.y).coerceAtLeast(0f)),CornerRadius(5.dp.toPx()))
            }
        }
        if(labels.isNotEmpty()) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            labels.forEachIndexed { index,label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    if(!line) Text((data.getOrNull(index) ?: 0f).toInt().toString(), style = MaterialTheme.typography.labelSmall, color = if((data.getOrNull(index) ?: 0f)>0f) Lime else FitMuted)
                    Text(label, style = MaterialTheme.typography.labelSmall, color = FitMuted)
                }
            }
        }
    }
}
