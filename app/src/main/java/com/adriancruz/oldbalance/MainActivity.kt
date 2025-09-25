package com.adriancruz.oldbalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.adriancruz.oldbalance.ui.screens.MainScreen
import com.adriancruz.oldbalance.ui.theme.OldBalanceTheme
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel
import com.adriancruz.oldbalance.ui.viewmodel.ViewModelFactory

/**
 * Actividad principal de la aplicacion.
 *
 * Esta actividad es el punto de entrada de la interfaz de usuario.
 */
class MainActivity : ComponentActivity() {

    /**
     * Instancia del [MainViewModel] utilizado por la actividad.
     *
     * Se obtiene a traves de un [ViewModelFactory] que le proporciona el repositorio de datos.
     */
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory((application as WeightApp).repository)
    }

    /**
     * Metodo que se llama cuando se crea la actividad.
     *
     * Aqui se configura el contenido de la interfaz de usuario utilizando Jetpack Compose.
     * @param savedInstanceState estado de la instancia guardado previamente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OldBalanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}