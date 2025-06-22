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
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.feryafox.yokailib.categories.Category
import ru.feryafox.yokailib.categories.CategoryItem
import ru.feryafox.yokailib.preferences.PreferencesManagerFactory
import ru.feryafox.yokailib.root.YOKAILIB_ID
import ru.feryafox.yokailib.root.ui.Routes
import ru.feryafox.yokailib.root.ui.components.popup.*
import ru.feryafox.yokailib.root.ui.viewmodels.MainScreenViewModel
import ru.feryafox.yokailib.utils.settingsFields

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainListScreen(
    navController: NavHostController,
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    val coroutine  = rememberCoroutineScope()
    val drawer     = rememberDrawerState(DrawerValue.Closed)
    val prefs      = remember { PreferencesManagerFactory.create(YOKAILIB_ID) }

    var selectedId by remember { mutableStateOf(prefs.getString("last_screen", "")) }
    var screenBody by remember { mutableStateOf<@Composable () -> Unit>({ Placeholder() }) }

    val errorTint  = MaterialTheme.colorScheme.errorContainer

    val popupState      = remember { mutableStateOf<PopupConfig?>(null) }
    val popupController = remember { PopupHostController(popupState) }

    CompositionLocalProvider(LocalPopupHost provides popupController) {

        fun showInvalidFieldsPopup(category: Category, item: CategoryItem) {
            val invalid = buildList {
                addAll(item.settingsFields)
                addAll(category.settingsFields)
            }.filter { it.needAttention(true) }

            if (invalid.isNotEmpty()) {
                popupController.show(
                    PopupConfig(
                        title          = "Требуются данные",
                        message        = "Заполните следующие поля",
                        tint           = errorTint,
                        dismissible    = false,
                        dimBackground  = true,
                        requiredFields = invalid,
                        inline         = true
                    )
                )
            }
        }

        LaunchedEffect(selectedId, viewModel.categories) {
            if (selectedId.isNotBlank()) {
                viewModel.categories.forEach { cat ->
                    cat.items.find { it.id == selectedId }?.let { itm ->
                        screenBody = { itm.Content() }
                        showInvalidFieldsPopup(cat, itm)
                        itm.onSelected()
                        return@LaunchedEffect
                    }
                }
            }
        }

        ModalNavigationDrawer(
            drawerState = drawer,
            drawerContent = {
                DrawerSheet(
                    vm        = viewModel,
                    errorTint = errorTint,
                    onSelect  = { cat, itm ->
                        showInvalidFieldsPopup(cat, itm)

                        screenBody = { itm.Content() }
                        selectedId = itm.id
                        prefs.setString("last_screen", itm.id)

                        coroutine.launch { drawer.close() }
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
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(cat, itm) }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(itm.title, style = MaterialTheme.typography.titleMedium)
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

    if (cfg.requiredFields.isNotEmpty() && !cfg.dismissible) {
        val stillInvalid by remember {
            derivedStateOf {
                cfg.requiredFields.any { it.needAttention(cfg.skipDisabled) }
            }
        }
        if (!stillInvalid) onDismiss()
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
