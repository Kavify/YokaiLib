package ru.feryafox.yokailib.root.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.feryafox.yokailib.categories.Category
import ru.feryafox.yokailib.categories.CategoryItem
import ru.feryafox.yokailib.preferences.PreferencesManagerFactory
import ru.feryafox.yokailib.root.YOKAILIB_ID
import ru.feryafox.yokailib.root.ui.Routes
import ru.feryafox.yokailib.root.ui.components.popup.*
import ru.feryafox.yokailib.root.ui.viewmodels.MainScreenViewModel
import ru.feryafox.yokailib.utils.ValidationMode
import ru.feryafox.yokailib.utils.ValidationResult
import ru.feryafox.yokailib.utils.isBlocked
import ru.feryafox.yokailib.utils.validateAsync

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainListScreen(
    navController: NavHostController,
    viewModel: MainScreenViewModel = hiltViewModel(),
    validationMode: ValidationMode = ValidationMode.ON_DRAWER_AND_SETTINGS
) {
    val coroutine  = rememberCoroutineScope()
    val drawer     = rememberDrawerState(DrawerValue.Closed)
    val prefs      = remember { PreferencesManagerFactory.create(YOKAILIB_ID) }

    var selectedId by remember { mutableStateOf(prefs.getString("last_screen", "")) }
    var screenBody by remember { mutableStateOf<@Composable () -> Unit>({ Placeholder() }) }

    val errorTint  = MaterialTheme.colorScheme.errorContainer

    val popupState      = remember { mutableStateOf<PopupConfig?>(null) }
    val popupController = remember { PopupHostController(popupState) }

    // Флаг возврата из настроек
    var returningFromSettings by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalPopupHost provides popupController) {

        suspend fun performValidationAsync(category: Category, item: CategoryItem) {
            // Показываем popup с загрузкой
            popupController.show(
                PopupConfig(
                    title = "Проверка данных",
                    message = "Выполняется валидация...",
                    tint = errorTint,
                    dismissible = false,
                    dimBackground = true,
                    inline = true,
                    isLoading = true
                )
            )

            // Выполняем асинхронную валидацию
            val categoryValidation = category.validateAsync()
            val itemValidation = item.validateAsync()

            val combinedResult = when {
                !categoryValidation.isValid -> categoryValidation
                !itemValidation.isValid -> itemValidation
                else -> ValidationResult.Success
            }

            // Обновляем popup с результатом
            if (combinedResult is ValidationResult.Failure) {
                val errorMessages = combinedResult.getAllMessages()
                popupController.show(
                    PopupConfig(
                        title = "Требуются данные",
                        message = errorMessages.joinToString("\n"),
                        tint = errorTint,
                        dismissible = false,
                        dimBackground = true,
                        inline = true,
                        validationResult = combinedResult,
                        isLoading = false
                    )
                )
            } else {
                // Валидация прошла успешно, закрываем popup
                popupController.dismiss()
            }
        }

        fun shouldValidate(trigger: ValidationMode): Boolean {
            return when (validationMode) {
                ValidationMode.ON_SCREEN_CHANGE -> trigger == ValidationMode.ON_SCREEN_CHANGE
                ValidationMode.ON_DRAWER_TOGGLE -> trigger == ValidationMode.ON_DRAWER_TOGGLE
                ValidationMode.ON_SETTINGS_EXIT -> trigger == ValidationMode.ON_SETTINGS_EXIT
                ValidationMode.ON_DRAWER_AND_SETTINGS ->
                    trigger == ValidationMode.ON_DRAWER_TOGGLE || trigger == ValidationMode.ON_SETTINGS_EXIT
                ValidationMode.MANUAL -> false
            }
        }

        // Валидация при изменении выбранного экрана
        LaunchedEffect(selectedId, viewModel.categories) {
            if (selectedId.isNotBlank() && shouldValidate(ValidationMode.ON_SCREEN_CHANGE)) {
                viewModel.categories.forEach { cat ->
                    cat.items.find { it.id == selectedId }?.let { itm ->
                        screenBody = { itm.Content() }
                        performValidationAsync(cat, itm)
                        itm.onSelected()
                        return@LaunchedEffect
                    }
                }
            } else if (selectedId.isNotBlank()) {
                // Просто устанавливаем экран без валидации
                viewModel.categories.forEach { cat ->
                    cat.items.find { it.id == selectedId }?.let { itm ->
                        screenBody = { itm.Content() }
                        itm.onSelected()
                        return@LaunchedEffect
                    }
                }
            }
        }

        // Валидация при закрытии drawer
        LaunchedEffect(drawer.currentValue) {
            if (drawer.currentValue == DrawerValue.Closed &&
                selectedId.isNotBlank() &&
                shouldValidate(ValidationMode.ON_DRAWER_TOGGLE)) {
                viewModel.categories.forEach { cat ->
                    cat.items.find { it.id == selectedId }?.let { itm ->
                        performValidationAsync(cat, itm)
                        return@LaunchedEffect
                    }
                }
            }
        }

        // Валидация при возврате из настроек
        LaunchedEffect(returningFromSettings) {
            if (returningFromSettings &&
                selectedId.isNotBlank() &&
                shouldValidate(ValidationMode.ON_SETTINGS_EXIT)) {
                viewModel.categories.forEach { cat ->
                    cat.items.find { it.id == selectedId }?.let { itm ->
                        performValidationAsync(cat, itm)
                        returningFromSettings = false
                        return@LaunchedEffect
                    }
                }
            }
        }

        // Слушаем навигацию для отслеживания возврата из настроек
        DisposableEffect(navController) {
            val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                if (destination.route != Routes.SETTING.path) {
                    returningFromSettings = true
                }
            }
            navController.addOnDestinationChangedListener(listener)
            onDispose {
                navController.removeOnDestinationChangedListener(listener)
            }
        }

        ModalNavigationDrawer(
            drawerState = drawer,
            drawerContent = {
                DrawerSheet(
                    vm        = viewModel,
                    errorTint = errorTint,
                    onSelect  = { cat, itm ->
                        coroutine.launch {
                            if (shouldValidate(ValidationMode.ON_SCREEN_CHANGE)) {
                                performValidationAsync(cat, itm)
                            }

                            screenBody = { itm.Content() }
                            selectedId = itm.id
                            prefs.setString("last_screen", itm.id)

                            drawer.close()
                        }
                    },
                    onSettings = {
                        navController.navigate(Routes.SETTING.path)
                        coroutine.launch { drawer.close() }
                    }
                )
            }
        ) {

            Box(Modifier.fillMaxSize()) {

                screenBody()

                popupState.value?.let { cfg ->
                    InlinePopupOverlay(cfg) { popupController.dismiss() }
                }
            }
        }
    }
}

@Composable
private fun DrawerSheet(
    vm: MainScreenViewModel,
    errorTint: Color,
    onSelect: (Category, CategoryItem) -> Unit,
    onSettings: () -> Unit
) {
    ModalDrawerSheet {
        LazyColumn(modifier = Modifier.weight(1f, fill = true)) {
            vm.categories.forEach { cat ->
                item("hdr_${cat.id}") {
                    Text(
                        text = cat.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(cat.items, key = { it.id }) { itm ->
                    val isBlocked = itm.isBlocked || cat.isBlocked

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(cat, itm) }
                            .then(
                                if (isBlocked) {
                                    Modifier.background(errorTint.copy(alpha = 0.1f))
                                } else {
                                    Modifier
                                }
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = itm.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isBlocked) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider()
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onSettings() }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text("Настройки", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun InlinePopupOverlay(
    cfg: PopupConfig,
    onDismiss: () -> Unit
) {
    val card: @Composable () -> Unit =
        { DefaultPopupCard(config = cfg) { onDismiss() } }

    Box(Modifier.fillMaxSize()) {

        if (cfg.dimBackground) {
            Box(
                Modifier
                    .matchParentSize()
                    .alpha(0.45f)
                    .background(cfg.tint.takeOrElse { Color.Black })
            )
        }

        Box(
            Modifier
                .padding(top = 32.dp)
                .align(Alignment.TopCenter)
        ) { card() }
    }

    // Автоматическое закрытие при успешной валидации
    if (!cfg.dismissible) {
        val shouldDismiss by remember {
            derivedStateOf {
                cfg.validationResult?.isValid == true
            }
        }
        if (shouldDismiss) onDismiss()
    }
}


@Composable
private fun Placeholder() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
    }
}
