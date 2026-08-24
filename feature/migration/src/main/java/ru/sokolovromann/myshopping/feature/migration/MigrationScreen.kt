package ru.sokolovromann.myshopping.feature.migration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.sokolovromann.myshopping.core.ui.component.ProgressIndicator

@Composable
fun MigrationScreen(viewModel: MigrationViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            MigrationUiState.Initial -> {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = stringResource(R.string.migration_header),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(stringResource(R.string.migration_text_initial))
                Button(
                    modifier = Modifier.padding(vertical = 32.dp),
                    onClick = { viewModel.onMigrate() },
                    content = { Text(stringResource(R.string.migration_btn_start)) }
                )
            }
            MigrationUiState.Migrating -> {
                ProgressIndicator(
                    text = stringResource(R.string.migration_text_migrating)
                )
            }
            MigrationUiState.Finish -> {
                Text(stringResource(R.string.migration_text_finish))
                Button(
                    modifier = Modifier.padding(vertical = 32.dp),
                    onClick = { viewModel.onOpenPurchases() },
                    content = { Text(stringResource(R.string.migration_btn_finish)) }
                )
            }
        }
    }

    BackHandler { viewModel.onCancel() }
}