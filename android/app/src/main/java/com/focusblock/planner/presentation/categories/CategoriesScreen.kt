package com.focusblock.planner.presentation.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusblock.planner.R
import com.focusblock.planner.core.di.AppViewModelFactory
import com.focusblock.planner.core.theme.BorderHair
import com.focusblock.planner.core.theme.SurfaceCard
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.theme.TextSecondary
import com.focusblock.planner.core.ui.BentoTile
import com.focusblock.planner.core.ui.EmptyStateCard
import com.focusblock.planner.core.ui.IconTile
import com.focusblock.planner.core.ui.ScreenScaffold
import com.focusblock.planner.core.ui.SectionHeader
import com.focusblock.planner.core.ui.SecondaryButton
import com.focusblock.planner.core.ui.argbColor
import com.focusblock.planner.core.ui.categorySprite
import com.focusblock.planner.core.ui.durationText
import com.focusblock.planner.presentation.dialog.ConfirmDialog

private val CategoriesOverlay = listOf(
    Color(0xE6141022),
    Color(0xF01B1533),
    Color(0xF5141022)
)

@Composable
fun CategoriesRoute(onBack: () -> Unit) {
    val categoriesViewModel: CategoriesViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by categoriesViewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        categoriesViewModel.refresh()
        onDispose { }
    }
    CategoriesScreen(
        state = state,
        onBack = onBack,
        onToggle = categoriesViewModel::toggleExpanded,
        onColorChange = categoriesViewModel::changeColor,
        onAdd = categoriesViewModel::addCategory,
        onRequestDelete = categoriesViewModel::requestDelete,
        onConfirmDelete = categoriesViewModel::confirmDelete,
        onDismissDelete = categoriesViewModel::dismissDelete
    )
}

@Composable
private fun CategoriesScreen(
    state: CategoriesUiState,
    onBack: () -> Unit,
    onToggle: (String) -> Unit,
    onColorChange: (String, Long) -> Unit,
    onAdd: () -> Unit,
    onRequestDelete: (String) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit
) {
    ScreenScaffold(overlay = CategoriesOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            SectionHeader(
                eyebrow = stringResource(R.string.categories_eyebrow),
                title = stringResource(R.string.categories_title),
                modifier = Modifier.fillMaxWidth(),
                leading = {
                    IconTile(
                        contentDescription = stringResource(R.string.cd_back),
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (state.isEmpty) {
                EmptyStateCard(
                    message = stringResource(R.string.categories_empty),
                    spriteRes = R.drawable.sprite_5,
                    spriteDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.category.id }) { item ->
                        CategoryCard(
                            item = item,
                            expanded = state.expandedId == item.category.id,
                            palette = state.palette,
                            onToggle = { onToggle(item.category.id) },
                            onColorChange = { color -> onColorChange(item.category.id, color) },
                            onDelete = { onRequestDelete(item.category.id) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_add_category),
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
    if (state.pendingDeleteId != null) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_delete_category_title),
            message = stringResource(R.string.confirm_delete_category_message),
            confirmLabel = stringResource(R.string.confirm_delete),
            dismissLabel = stringResource(R.string.confirm_cancel),
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }
}

@Composable
private fun CategoryCard(
    item: CategoryItem,
    expanded: Boolean,
    palette: List<Long>,
    onToggle: () -> Unit,
    onColorChange: (Long) -> Unit,
    onDelete: () -> Unit
) {
    val color = argbColor(item.category.colorArgb)
    BentoTile(modifier = Modifier.fillMaxWidth(), onClick = onToggle) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(categorySprite(item.category.spriteIndex)),
                    contentDescription = stringResource(
                        R.string.cd_category,
                        item.category.name
                    ),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (item.weekMinutes > 0) {
                        stringResource(
                            R.string.categories_week,
                            durationText(item.weekMinutes)
                        )
                    } else {
                        stringResource(R.string.categories_week_empty)
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = TextMuted,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(180)) + fadeIn(tween(180)),
            exit = shrinkVertically(tween(160)) + fadeOut(tween(120))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    palette.take(4).forEachIndexed { index, value ->
                        val swatch = argbColor(value)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(swatch)
                                .border(
                                    width = if (value == item.category.colorArgb) 2.dp else 1.dp,
                                    color = if (value == item.category.colorArgb) {
                                        TextPrimary
                                    } else {
                                        BorderHair
                                    },
                                    shape = CircleShape
                                )
                                .clickable(
                                    onClickLabel = stringResource(R.string.cd_swatch, index + 1)
                                ) { onColorChange(value) }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceCard)
                            .border(1.dp, BorderHair, RoundedCornerShape(14.dp))
                            .clickable(onClick = onDelete),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(
                                R.string.confirm_delete_category_title
                            ),
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
