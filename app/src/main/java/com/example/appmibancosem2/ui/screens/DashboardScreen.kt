package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.navigation.Screen
import com.example.appmibancosem2.ui.theme.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.History
@Composable
fun DashboardScreen(
    onNavigateTo: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = { MiBancoTopBar(titulo = "Mi Banco") }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // 👤 Bienvenida
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Bienvenido, ${DemoData.cuenta.titular.split(" ").first()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyDark
                )
            }

            TarjetaCuenta(cuenta = DemoData.cuenta)

            // 📌 ACCESOS RÁPIDOS
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = "Accesos rápidos",
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark,
                        fontSize = 15.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    // 🔹 Primera fila
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BotonAccesoRapido(
                            icono = Icons.Default.Receipt,
                            etiqueta = "Movimientos",
                            color = NavyPrimary,
                            onClick = { onNavigateTo(Screen.Transacciones) }
                        )
                        BotonAccesoRapido(
                            icono = Icons.Default.Payment,
                            etiqueta = "Pagar",
                            color = GreenPositive,
                            onClick = { onNavigateTo(Screen.Pagos) }
                        )
                        BotonAccesoRapido(
                            icono = Icons.Default.AccountBalance,
                            etiqueta = "Préstamos",
                            color = GoldAccent,
                            onClick = { onNavigateTo(Screen.Prestamos) }
                        )
                        BotonAccesoRapido(
                            icono = Icons.Default.Savings,
                            etiqueta = "Ahorro",
                            color = NavyLight,
                            onClick = { onNavigateTo(Screen.Ahorro) }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // 🔥 SEGUNDA FILA CORREGIDA (Crédito + Historial)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BotonAccesoRapido(
                            icono = Icons.Default.RequestPage,
                            etiqueta = "Crédito",
                            color = GreenPositive,
                            onClick = { onNavigateTo(Screen.SolicitudCredito) }
                        )
                        BotonAccesoRapido(
                            icono = Icons.Default.History,
                            etiqueta = "Historial",
                            color = NavyPrimary,
                            onClick = { onNavigateTo(Screen.Historial) }
                        )
                    }
                }
            }

            // 📊 Últimos movimientos
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Últimos movimientos",
                            fontWeight = FontWeight.SemiBold,
                            color = NavyDark
                        )

                        TextButton(onClick = { onNavigateTo(Screen.Transacciones) }) {
                            Text("Ver todos", color = GoldAccent, fontSize = 12.sp)
                        }
                    }

                    DemoData.transacciones.take(3).forEach { tx ->
                        FilaTransaccion(transaccion = tx)
                    }
                }
            }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}
