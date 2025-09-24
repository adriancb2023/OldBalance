package com.adriancruz.oldbalance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel

@Composable
fun DeveloperScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Developer Options", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { viewModel.insertTestData() }) {
            Text("Insert Test Data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.deleteAllData() }) {
            Text("Delete All Data")
        }
    }
}
