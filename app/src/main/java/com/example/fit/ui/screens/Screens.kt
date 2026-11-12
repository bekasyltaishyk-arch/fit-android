package com.example.fit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fit.R
import com.example.fit.data.local.WorkoutCatalog
import com.example.fit.data.model.*
import com.example.fit.ui.components.*
import com.example.fit.ui.theme.*
import com.example.fit.ui.viewmodel.*
import com.example.fit.utils.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

fun date(ms: Long): String = if(ms==0L) "—" else Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm",Locale.forLanguageTag("kk-KZ")))
private fun dayLabels(): List<String> = (6 downTo 0).map { LocalDate.now().minusDays(it.toLong()).format(DateTimeFormatter.ofPattern("EE",Locale.forLanguageTag("kk-KZ"))) }

@Composable fun Home(s: FitState, detail: (String)->Unit, progress: ()->Unit) {
    val stats = statistics(s.workouts)
    val recommended = WorkoutCatalog.all.first { it.id == "full" }
    Page("Сәлем, ${s.profile?.name?.ifBlank { "спортшы" } ?: "спортшы"}!", subtitle = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, EEEE",Locale.forLanguageTag("kk-KZ"))), showBrand = true) {
        FitHero(recommended) { detail(recommended.id) }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FitStatCard(stats.total.toString(), stringResource(R.string.completed_label), Icons.Default.DoneAll, Modifier.weight(1f).fillMaxHeight())
            FitStatCard("%.1f".format(stats.minutes), stringResource(R.string.activity_minutes), Icons.Default.Timer, Modifier.weight(1f).fillMaxHeight())
        }
        FitSectionHeader(stringResource(R.string.week_activity), stringResource(R.string.fit_text_030), progress)
        FitPanel {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.last_seven_days), color = FitMuted, style = MaterialTheme.typography.bodyMedium)
                Text("${stats.week} жаттығу", color = Lime, style = MaterialTheme.typography.labelLarge)
            }
            Chart(stats.days.map { it.toFloat() }, labels = dayLabels())
        }
        FitSectionHeader(stringResource(R.string.last_workout))
        val last = s.workouts.firstOrNull()
        if(last == null) FitEmptyState(stringResource(R.string.fit_text_025), stringResource(R.string.fit_text_026), action = stringResource(R.string.fit_text_029), onAction = {detail("full")})
        else FitCard(kazakhTitle(last), "${date(last.completedAt)} • ${"%.1f".format(last.durationMinutes)} мин")
        FitSectionHeader(stringResource(R.string.recommended_workouts))
        FitWorkoutCard(recommended) { detail(recommended.id) }
    }
}

@Composable fun Catalog(detail: (String)->Unit) {
    val allLabel = stringResource(R.string.fit_text_030)
    val levels = listOf(allLabel, stringResource(R.string.fit_text_034), stringResource(R.string.fit_text_035))
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(allLabel) }
    var level by rememberSaveable { mutableStateOf(allLabel) }
    val filtered = WorkoutCatalog.all.filter { it.title.contains(query.trim(),true) && (category == allLabel || it.category == category) && (level == allLabel || it.level == level) }
    LazyColumn(Modifier.fillMaxSize().imePadding(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FitBrand(); Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.fit_text_031), style = MaterialTheme.typography.headlineLarge)
                Text(stringResource(R.string.catalog_subtitle), color = FitMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            OutlinedTextField(query, {query=it}, modifier = Modifier.fillMaxWidth(), placeholder = {Text(stringResource(R.string.fit_text_033))}, leadingIcon = {Icon(Icons.Default.Search,null)}, trailingIcon = {if(query.isNotEmpty()) IconButton({query=""}) {Icon(Icons.Default.Close,stringResource(R.string.clear_search))}}, singleLine = true, shape = RoundedCornerShape(16.dp))
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf(allLabel)+StoredIds.categories, key = {it}) { id ->
                    FilterChip(category == id, {category=id}, label = {Text(kazakhLabel(id))}, leadingIcon = if(category==id) {{Icon(Icons.Default.Check,null,Modifier.size(16.dp))}} else null)
                }
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(levels) { value -> FilterChip(level==value,{level=value},label={Text(value)}) }
            }
        }
        item { Text("${filtered.size} жаттығу", color = FitMuted, style = MaterialTheme.typography.labelMedium) }
        if(filtered.isEmpty()) item { FitEmptyState(stringResource(R.string.search_empty_title), stringResource(R.string.fit_text_036), Icons.Default.SearchOff) }
        items(filtered, key = {it.id}) { w -> FitWorkoutCard(w) {detail(w.id)} }
    }
}

@Composable fun Detail(w: Workout, canStart: Boolean, start: ()->Unit) {
    val context = LocalContext.current
    Page(stringResource(R.string.workout_plan)) {
        WorkoutImage(w.category, Modifier.height(220.dp))
        FitBadge(kazakhLabel(w.category))
        Text(w.title, style = MaterialTheme.typography.headlineMedium)
        WorkoutMeta(w)
        Text(w.description, color = FitMuted, style = MaterialTheme.typography.bodyLarge)
        FitPanel {
            Icon(Icons.Default.Info, null, Modifier.size(20.dp), tint = Lime)
            Text(stringResource(R.string.fit_text_037), style = MaterialTheme.typography.bodyMedium, color = FitMuted)
        }
        FitSectionHeader(stringResource(R.string.exercise_sequence))
        w.exercises.forEachIndexed { i,e ->
            FitPanel {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(32.dp), shape = CircleShape, color = Lime.copy(alpha = .12f)) {Box(contentAlignment = Alignment.Center) {Text("${i+1}",color = Lime, style = MaterialTheme.typography.labelLarge)}}
                    Text(e.name, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                }
                Text("${e.sets} тәсіл • ${e.reps}",color = Lime, style = MaterialTheme.typography.labelLarge)
                Text(e.instruction, color = FitMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
        FitButton(context.getString(R.string.fit_text_029),start,enabled=canStart,icon=Icons.Default.PlayArrow)
        if(!canStart) Text(stringResource(R.string.fit_text_038),color=FitMuted,style=MaterialTheme.typography.bodyMedium)
    }
}

@Composable fun Active(w: Workout, vm: FitViewModel, s: FitState, done: ()->Unit, exit: ()->Unit) {
    val context = LocalContext.current
    val index by vm.exerciseIndex.collectAsState()
    val sets by vm.setIndex.collectAsState()
    var confirm by remember {mutableStateOf(false)}
    var early by remember {mutableStateOf(false)}
    var setLocked by remember {mutableStateOf(false)}
    LaunchedEffect(setLocked) {if(setLocked) {kotlinx.coroutines.delay(500);setLocked=false}}
    val e = w.exercises.getOrNull(index) ?: return
    BackHandler {if(!s.busy) confirm=true}
    if(confirm) AlertDialog(onDismissRequest={confirm=false}, title={Text(stringResource(R.string.fit_text_039))}, text={Text(stringResource(R.string.fit_text_040))}, confirmButton={TextButton({vm.clearSession();confirm=false;exit()},enabled=!s.busy) {Text(stringResource(R.string.fit_text_041))}},dismissButton={TextButton({confirm=false}) {Text(stringResource(R.string.fit_text_042))}})
    if(early) AlertDialog(onDismissRequest={early=false},title={Text(stringResource(R.string.fit_text_043))},text={Text(stringResource(R.string.fit_text_044))},confirmButton={TextButton({early=false;vm.finish(w,done)},enabled=!s.busy) {Text(stringResource(R.string.fit_text_045))}},dismissButton={TextButton({early=false}) {Text(stringResource(R.string.fit_text_042))}})
    Page(stringResource(R.string.fit_text_046),subtitle=w.title) {
        WorkoutImage(w.category,Modifier.height(160.dp))
        FitPanel {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                Text("${index+1} / ${w.exercises.size}",color=Lime,style=MaterialTheme.typography.headlineMedium)
                Text(stringResource(R.string.exercise_label),color=FitMuted,style=MaterialTheme.typography.labelLarge)
            }
            FitProgressIndicator((index+sets.toFloat()/e.sets)/w.exercises.size)
        }
        Text(e.name,style=MaterialTheme.typography.headlineMedium)
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            FitStatCard("${sets.coerceAtMost(e.sets)} / ${e.sets}",stringResource(R.string.sets_label),Icons.Default.Repeat,Modifier.weight(1f).fillMaxHeight())
            FitStatCard(e.reps,stringResource(R.string.repetitions_label),Icons.Default.FitnessCenter,Modifier.weight(1f).fillMaxHeight())
        }
        FitPanel {
            Text(stringResource(R.string.instructions_label),style=MaterialTheme.typography.titleMedium)
            Text(e.instruction,color=FitMuted,style=MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.fit_text_047),color=FitMuted,style=MaterialTheme.typography.bodySmall)
        }
        FitButton(context.getString(R.string.fit_text_048),{setLocked=true;vm.setDone(w)},enabled=sets<e.sets && !s.busy && !setLocked,icon=Icons.Default.Check)
        if(index<w.exercises.lastIndex) FitButton(context.getString(R.string.fit_text_049),{vm.next(w)},enabled=sets>=e.sets && !s.busy)
        OutlinedButton({if(index==w.exercises.lastIndex && sets>=e.sets) vm.finish(w,done) else early=true},Modifier.fillMaxWidth().heightIn(min=50.dp),enabled=!s.busy,shape=RoundedCornerShape(16.dp)) {Text(stringResource(R.string.fit_text_050))}
        TextButton({confirm=true},enabled=!s.busy,modifier=Modifier.align(Alignment.CenterHorizontally)) {Text(stringResource(R.string.fit_text_051),color=FitMuted)}
    }
}

@Composable fun Progress(s: FitState, vm: FitViewModel, pounds: Boolean) {
    val context = LocalContext.current
    val stats = statistics(s.workouts)
    var weight by rememberSaveable {mutableStateOf("")}
    var shown by rememberSaveable {mutableIntStateOf(10)}
    var weightsShown by rememberSaveable {mutableIntStateOf(5)}
    val factor = if(pounds) 2.2046226218 else 1.0
    val unit = if(pounds) stringResource(R.string.fit_text_052) else stringResource(R.string.fit_text_053)
    Page(stringResource(R.string.fit_text_054),subtitle=stringResource(R.string.progress_subtitle)) {
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            FitStatCard(stats.total.toString(),stringResource(R.string.completed_label),Icons.Default.DoneAll,Modifier.weight(1f).fillMaxHeight())
            FitStatCard("%.1f".format(stats.minutes),stringResource(R.string.activity_minutes),Icons.Default.Timer,Modifier.weight(1f).fillMaxHeight())
        }
        FitPanel {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {Text("Апта: ${stats.week}",color=Lime);Text("Ай: ${stats.month}",color=FitMuted)}
            Text(stringResource(R.string.fit_text_055),style=MaterialTheme.typography.titleMedium)
            Chart(stats.days.map{it.toFloat()},labels=dayLabels())
        }
        FitCard(stringResource(R.string.milestone_title),when {stats.total>=10 -> context.getString(R.string.fit_text_056);stats.total>=1 -> context.getString(R.string.fit_text_057);else -> context.getString(R.string.fit_text_058)})
        FitSectionHeader(stringResource(R.string.weight_change))
        FitPanel {
            if(s.weights.isEmpty()) Text(stringResource(R.string.fit_text_061),color=FitMuted,style=MaterialTheme.typography.bodyMedium)
            else {
                Text("${"%.1f".format(s.weights.last().weight*factor)} $unit",style=MaterialTheme.typography.headlineMedium,color=Lime)
                val visible=s.weights.takeLast(30)
                Chart(visible.map{(it.weight*factor).toFloat()},line=true)
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {Text(date(visible.first().recordedAt),style=MaterialTheme.typography.labelSmall,color=FitMuted);Text(date(visible.last().recordedAt),style=MaterialTheme.typography.labelSmall,color=FitMuted)}
            }
            Field(weight,"Қазіргі салмақ ($unit)",{weight=it},true)
            FitButton(context.getString(R.string.fit_text_060),{
                val kg=weight.replace(',','.').toDoubleOrNull()?.div(factor)
                if(kg==null || !kg.isFinite() || kg !in 20.0..400.0) vm.message(context.getString(R.string.fit_text_059))
                else {vm.weight(kg);weight=""}
            },busy=s.busy,icon=Icons.Default.Add)
        }
        if(s.weights.isNotEmpty()) {
            FitSectionHeader(stringResource(R.string.weight_history))
            FitPanel {
                s.weights.asReversed().take(weightsShown).forEach { record -> Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {Text(date(record.recordedAt),Modifier.weight(1f),style=MaterialTheme.typography.bodySmall,color=FitMuted);Text("${"%.1f".format(record.weight*factor)} $unit",style=MaterialTheme.typography.labelLarge)} }
                if(s.weights.size>weightsShown) TextButton({weightsShown+=10}) {Text(stringResource(R.string.show_more))}
            }
        }
        FitSectionHeader(stringResource(R.string.fit_text_062))
        if(s.workouts.isEmpty()) FitEmptyState(stringResource(R.string.history_empty_title),stringResource(R.string.fit_text_063),Icons.Default.History)
        s.workouts.take(shown).forEach {FitCard(kazakhTitle(it),"${date(it.completedAt)} • ${"%.1f".format(it.durationMinutes)} мин\n${kazakhLabel(it.category)}")}
        if(s.workouts.size>shown) OutlinedButton({shown+=10},Modifier.fillMaxWidth()) {Text(stringResource(R.string.show_more))}
    }
}

@Composable fun ProfileScreen(s: FitState, vm: FitViewModel, settings: ()->Unit, pounds: Boolean) {
    val context=LocalContext.current
    val p=s.profile
    if(p==null) {Page(stringResource(R.string.fit_text_064)) {FitEmptyState(stringResource(R.string.profile_unavailable),stringResource(R.string.fit_text_065),Icons.Default.CloudOff, stringResource(R.string.fit_text_066),{vm.logout()})};return}
    val factor=if(pounds) 2.2046226218 else 1.0
    var name by rememberSaveable(p.name) {mutableStateOf(p.name)}
    var age by rememberSaveable(p.age) {mutableStateOf(p.age.takeIf{it>0}?.toString() ?: "")}
    var height by rememberSaveable(p.height) {mutableStateOf(p.height.takeIf{it>0}?.toString() ?: "")}
    var weight by rememberSaveable(p.weight,pounds) {mutableStateOf(p.weight.takeIf{it>0}?.let {"%.1f".format(Locale.US,it*factor)} ?: "")}
    var goal by rememberSaveable(p.goal) {mutableStateOf(StoredIds.goal(p.goal) ?: p.goal)}
    Page(stringResource(R.string.fit_text_064),subtitle=stringResource(R.string.profile_subtitle)) {
        FitPanel {
            Surface(Modifier.size(56.dp),shape=CircleShape,color=Lime.copy(alpha=.13f)) {Box(contentAlignment=Alignment.Center) {Text(p.name.take(1).uppercase(Locale.forLanguageTag("kk-KZ")),style=MaterialTheme.typography.headlineMedium,color=Lime)}}
            Text(p.name,style=MaterialTheme.typography.titleLarge)
            Text(p.email,color=FitMuted,style=MaterialTheme.typography.bodyMedium)
            Text("Тіркелген күні: ${date(p.createdAt)}",color=FitMuted,style=MaterialTheme.typography.bodySmall)
            FitBadge("${s.workouts.size} жаттығу",Icons.Default.DoneAll)
        }
        FitSectionHeader(stringResource(R.string.personal_details))
        FitPanel {
            Field(name,context.getString(R.string.fit_text_009),{name=it})
            Field(age,context.getString(R.string.fit_text_067),{age=it},true)
            Field(height,context.getString(R.string.fit_text_068),{height=it},true)
            Field(weight,if(pounds) context.getString(R.string.fit_text_069) else context.getString(R.string.fit_text_070),{weight=it},true)
        }
        FitSectionHeader(stringResource(R.string.fitness_goal))
        FitPanel {
            StoredIds.goals.forEach { id ->
                Surface(onClick={goal=id},shape=RoundedCornerShape(14.dp),color=if(goal==id) Lime.copy(alpha=.1f) else FitSurface,border=BorderStroke(1.dp,if(goal==id) Lime.copy(alpha=.5f) else Color(0xFF30343A))) {
                    Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                        Icon(if(goal==id) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,null,Modifier.size(20.dp),tint=if(goal==id) Lime else FitMuted)
                        Text(kazakhLabel(id),style=MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        FitButton(context.getString(R.string.fit_text_045),{
            val a=age.toIntOrNull();val h=height.replace(',','.').toDoubleOrNull();val kg=weight.replace(',','.').toDoubleOrNull()?.div(factor)
            if(name.trim().length !in 2..60 || a==null || a !in 14..100 || h==null || !h.isFinite() || h !in 100.0..250.0 || kg==null || !kg.isFinite() || kg !in 20.0..400.0) vm.message(context.getString(R.string.fit_text_071))
            else vm.save(p.copy(name=name.trim(),age=a,height=h,weight=kg,goal=goal))
        },busy=s.busy,icon=Icons.Default.Check)
        OutlinedButton(settings,Modifier.fillMaxWidth().heightIn(min=50.dp),shape=RoundedCornerShape(16.dp)) {Icon(Icons.Default.Settings,null,Modifier.size(18.dp));Spacer(Modifier.width(8.dp));Text(stringResource(R.string.fit_text_072))}
    }
}
