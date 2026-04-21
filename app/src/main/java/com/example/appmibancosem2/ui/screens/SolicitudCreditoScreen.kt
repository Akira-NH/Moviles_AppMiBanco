package com.example.appmibancosem2.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.ui.theme.*
import com.example.appmibancosem2.data.model.local.SolicitudDatabase
import com.example.appmibancosem2.data.model.SolicitudCredito

private const val PREFS_SOLICITUD = "borrador_solicitud"
private const val KEY_MONTO = "sol_monto"
private const val KEY_PLAZO = "sol_plazo"
private const val KEY_TIPO = "sol_tipo"
private const val KEY_DNI = "sol_dni"

@Composable
fun SolicitudCreditoScreen(onBack: () -> Unit) {

    val contexto = LocalContext.current

    var monto by remember { mutableStateOf("") }
    var plazo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }

    var mostrarDialogoEnvio by remember { mutableStateOf(false) }
    var textoHistorial by remember { mutableStateOf("") }
    var mostrarDialogoHistorial by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefs = contexto.getSharedPreferences(PREFS_SOLICITUD, Context.MODE_PRIVATE)
        monto = prefs.getString(KEY_MONTO, "") ?: ""
        plazo = prefs.getString(KEY_PLAZO, "") ?: ""
        tipo = prefs.getString(KEY_TIPO, "") ?: ""
        dni = prefs.getString(KEY_DNI, "") ?: ""
    }

    fun guardar(clave: String, valor: String) {
        contexto.getSharedPreferences(PREFS_SOLICITUD, Context.MODE_PRIVATE)
            .edit().putString(clave, valor).apply()
    }

    fun limpiarBorrador() {
        contexto.getSharedPreferences(PREFS_SOLICITUD, Context.MODE_PRIVATE)
            .edit().clear().apply()
        monto = ""; plazo = ""; tipo = ""; dni = ""
    }

    Scaffold(
        topBar = {
            MiBancoTopBar(
                titulo = "Solicitud de Crédito",
                mostrarBack = true,
                onBack = onBack
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            CampoSolicitud("Monto", monto, "15000", KeyboardType.Number) {
                monto = it; guardar(KEY_MONTO, it)
            }

            CampoSolicitud("Plazo", plazo, "24", KeyboardType.Number) {
                plazo = it; guardar(KEY_PLAZO, it)
            }

            CampoSolicitud("Tipo", tipo, "Personal", KeyboardType.Text) {
                tipo = it; guardar(KEY_TIPO, it)
            }

            CampoSolicitud("DNI", dni, "12345678", KeyboardType.Number) {
                if (it.length <= 8) {
                    dni = it; guardar(KEY_DNI, it)
                }
            }

            if (mensajeError.isNotEmpty()) {
                Text(mensajeError, color = RedNegative)
            }

            Button(
                onClick = {
                    if (monto.isEmpty() || plazo.isEmpty() || tipo.isEmpty() || dni.isEmpty()) {
                        mensajeError = "Completa todos los campos"
                    } else {
                        mensajeError = ""
                        mostrarDialogoEnvio = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Solicitud")
            }

            OutlinedButton(
                onClick = {
                    textoHistorial = LogManager.obtenerHistorial(contexto)
                    mostrarDialogoHistorial = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Historial")
            }
        }
    }

    // 🔥 DIÁLOGO CORREGIDO (AQUÍ VA SQLITE)
    if (mostrarDialogoEnvio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEnvio = false },
            confirmButton = {
                Button(
                    onClick = {

                        // 1️⃣ Guardar en archivo (YA EXISTÍA)
                        val detalle =
                            "Monto: S/ $monto | Plazo: $plazo meses | Tipo: $tipo | DNI: $dni"
                        LogManager.registrar(contexto, detalle)

                        // 2️⃣ 🔥 NUEVO: guardar en SQLite
                        val sqlDb = SolicitudDatabase(contexto)
                        sqlDb.insertar(
                            SolicitudCredito(
                                monto      = monto.toDoubleOrNull() ?: 0.0,
                                plazoMeses = plazo.toIntOrNull() ?: 0,
                                tipo       = tipo,
                                dni        = dni,
                                estado     = "pendiente"
                            )
                        )

                        // 3️⃣ limpiar
                        limpiarBorrador()
                        mostrarDialogoEnvio = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            title = { Text("Enviado") },
            text = { Text("Solicitud registrada correctamente") }
        )
    }

    if (mostrarDialogoHistorial) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoHistorial = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoHistorial = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Historial") },
            text = { Text(textoHistorial) }
        )
    }
}

@Composable
fun CampoSolicitud(
    label: String,
    valor: String,
    hint: String,
    teclado: KeyboardType,
    onCambia: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambia,
        label = { Text(label) },
        placeholder = { Text(hint) },
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = Modifier.fillMaxWidth()
    )
}
