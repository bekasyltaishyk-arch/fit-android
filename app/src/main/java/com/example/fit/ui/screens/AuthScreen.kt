package com.example.fit.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.fit.R
import com.example.fit.ui.components.*
import com.example.fit.ui.theme.*
import com.example.fit.ui.viewmodel.*
import com.example.fit.utils.Validation

@Composable fun Page(title: String, subtitle: String? = null, showBrand: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal=18.dp,vertical=18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        if(showBrand) {FitBrand();Spacer(Modifier.height(2.dp))}
        Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Text(title,style=MaterialTheme.typography.headlineLarge)
            if(subtitle != null) Text(subtitle,style=MaterialTheme.typography.bodyMedium,color=FitMuted)
        }
        content()
        Spacer(Modifier.height(8.dp))
    }
}
@Composable fun Field(value: String, label: String, change: (String)->Unit, numeric: Boolean=false) {
    OutlinedTextField(value,change,label={Text(label)},modifier=Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(14.dp),keyboardOptions=KeyboardOptions(keyboardType=if(numeric) KeyboardType.Decimal else KeyboardType.Text))
}
@Composable fun AuthScreen(vm: FitViewModel, state: FitState, catalog: ()->Unit) {
    val context=LocalContext.current
    var register by rememberSaveable {mutableStateOf(false)}
    var name by rememberSaveable {mutableStateOf("")}
    var email by rememberSaveable {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    var confirm by remember {mutableStateOf("")}
    var visible by rememberSaveable {mutableStateOf(false)}
    fun valid(): Boolean {
        val error=when {register && name.trim().length !in 2..60 -> context.getString(R.string.fit_text_001);!Validation.email(email) -> context.getString(R.string.fit_text_002);!Validation.password(password) -> context.getString(R.string.fit_text_003);register && !Validation.matches(password,confirm) -> context.getString(R.string.fit_text_004);else -> null}
        vm.message(error);return error==null
    }
    Page(if(register) stringResource(R.string.fit_text_006) else stringResource(R.string.auth_welcome),subtitle=stringResource(R.string.auth_subtitle),showBrand=true) {
        WorkoutImage("full_body",Modifier.height(150.dp))
        if(!vm.configured) FitPanel {
            Icon(Icons.Default.CloudOff,null,tint=MaterialTheme.colorScheme.error)
            Text(stringResource(R.string.fit_text_008),color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.bodyMedium)
        }
        FitPanel {
            if(register) Field(name,context.getString(R.string.fit_text_009),{name=it})
            OutlinedTextField(email,{email=it},label={Text(stringResource(R.string.fit_text_010))},leadingIcon={Icon(Icons.Default.AlternateEmail,null)},singleLine=true,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(14.dp),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email))
            OutlinedTextField(password,{password=it},label={Text(stringResource(R.string.fit_text_011))},singleLine=true,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(14.dp),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),visualTransformation=if(visible) VisualTransformation.None else PasswordVisualTransformation(),trailingIcon={IconButton({visible=!visible}) {Icon(if(visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,if(visible) stringResource(R.string.fit_text_012) else stringResource(R.string.fit_text_013))}})
            if(register) OutlinedTextField(confirm,{confirm=it},label={Text(stringResource(R.string.fit_text_014))},singleLine=true,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(14.dp),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),visualTransformation=if(visible) VisualTransformation.None else PasswordVisualTransformation())
            FitButton(if(register) stringResource(R.string.fit_text_015) else stringResource(R.string.fit_text_016),{if(valid()) {if(register) vm.register(name,email,password) else vm.login(email,password)}},enabled=vm.configured,busy=state.busy)
            if(!register) TextButton({if(Validation.email(email)) vm.reset(email) else vm.message(context.getString(R.string.fit_text_019))},enabled=vm.configured && !state.busy,modifier=Modifier.align(Alignment.End)) {Text(stringResource(R.string.fit_text_020),style=MaterialTheme.typography.labelMedium)}
        }
        TextButton({register=!register},enabled=!state.busy,modifier=Modifier.align(Alignment.CenterHorizontally)) {Text(if(register) stringResource(R.string.fit_text_017) else stringResource(R.string.fit_text_018))}
        HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
        OutlinedButton(catalog,Modifier.fillMaxWidth().heightIn(min=50.dp),shape=RoundedCornerShape(16.dp)) {Text(stringResource(R.string.fit_text_021))}
    }
}
