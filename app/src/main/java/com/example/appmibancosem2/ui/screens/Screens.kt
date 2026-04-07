package com.example.appmibancosem2.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.data.model.SimuladorPrestamo
import com.example.appmibancosem2.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// M3 - HU-06: Historial de Transacciones
// ═══════════════════════════════════════════════════════════════
@Composable
fun TransaccionesScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            MiBancoTopBar(titulo = "Movimientos", mostrarBack = true, onBack = onBack)
        }
    ) { padding ->
        LazyColumn(
            modifier       = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                TarjetaCuenta(cuenta = DemoData.cuenta)
                Spacer(Modifier.height(16.dp))
                Text(
                    text       = "Historial de Movimientos",
                    fontWeight = FontWeight.Bold,
                    color      = NavyDark,
                    fontSize   = 16.sp
                )
                Spacer(Modifier.height(8.dp))
            }
            items(DemoData.transacciones) { tx ->
                FilaTransaccion(transaccion = tx)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// M4 - HU-08: Pago de Servicios
// ═══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosScreen(onBack: () -> Unit) {
    var servicioSeleccionado by remember { mutableStateOf(DemoData.servicios.first().nombre) }
    var expandido            by remember { mutableStateOf(false) }
    var numeroContrato       by remember { mutableStateOf("") }
    var monto                by remember { mutableStateOf("") }
    var mostrarModal         by remember { mutableStateOf(false) }
    var pagoRealizado        by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MiBancoTopBar(titulo = "Pago de Servicios", mostrarBack = true, onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (pagoRealizado) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape  = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = GreenPositive)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "¡Pago realizado con éxito!",
                            color      = GreenPositive,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text("Datos del servicio", fontWeight = FontWeight.SemiBold, color = NavyDark)

            ExposedDropdownMenuBox(
                expanded         = expandido,
                onExpandedChange = { expandido = !expandido }
            ) {
                OutlinedTextField(
                    value         = servicioSeleccionado,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Servicio") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expandido) },
                    modifier      = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded         = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                    DemoData.servicios.forEach { srv ->
                        DropdownMenuItem(
                            text    = { Text(srv.nombre) },
                            onClick = { servicioSeleccionado = srv.nombre; expandido = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value         = numeroContrato,
                onValueChange = { numeroContrato = it },
                label         = { Text("Número de contrato") },
                leadingIcon   = { Icon(Icons.Default.Article, null, tint = NavyPrimary) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value         = monto,
                onValueChange = { monto = it },
                label         = { Text("Monto a pagar (S/)") },
                leadingIcon   = { Icon(Icons.Default.AttachMoney, null, tint = NavyPrimary) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Button(
                onClick  = { mostrarModal = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled  = numeroContrato.isNotBlank() && monto.isNotBlank(),
                colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Text("Confirmar Pago", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarModal) {
        AlertDialog(
            onDismissRequest = { mostrarModal = false },
            title = { Text("Confirmar pago", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Servicio: $servicioSeleccionado")
                    Text("Contrato: $numeroContrato")
                    Text("Monto: S/ $monto")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarModal   = false
                        pagoRealizado  = true
                        numeroContrato = ""
                        monto          = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) { Text("Confirmar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarModal = false }) { Text("Cancelar") }
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// M5 - HU-10: Simulador de Préstamos
// ═══════════════════════════════════════════════════════════════
@Composable
fun PrestamosScreen(onBack: () -> Unit) {
    var monto      by remember { mutableStateOf(5000f) }
    var plazoIndex by remember { mutableStateOf(1) }
    var tasaIndex  by remember { mutableStateOf(0) }

    val plazos = listOf(6, 12, 18, 24, 36, 48)
    val tasas  = listOf(18.0, 24.0, 30.0, 36.0)

    val cuota by remember(monto, plazoIndex, tasaIndex) {
        derivedStateOf {
            SimuladorPrestamo(
                monto     = monto.toDouble(),
                tasaAnual = tasas[tasaIndex],
                cuotas    = plazos[plazoIndex]
            ).calcularCuota()
        }
    }

    Scaffold(
        topBar = {
            MiBancoTopBar(titulo = "Simulador de Préstamos", mostrarBack = true, onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape  = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Cuota mensual estimada", color = GoldLight, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = "S/ %,.2f".format(cuota),
                        fontSize   = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                    Text(
                        text  = "por ${plazos[plazoIndex]} meses",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }

            Column {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Monto del préstamo", fontWeight = FontWeight.Medium, color = NavyDark)
                    Text("S/ %,.0f".format(monto), fontWeight = FontWeight.Bold, color = NavyPrimary)
                }
                Slider(
                    value         = monto,
                    onValueChange = { monto = it },
                    valueRange    = 1000f..50000f,
                    steps         = 48,
                    colors        = SliderDefaults.colors(
                        activeTrackColor = NavyPrimary,
                        thumbColor       = NavyPrimary
                    )
                )
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("S/ 1,000", fontSize = 11.sp, color = GrayMedium)
                    Text("S/ 50,000", fontSize = 11.sp, color = GrayMedium)
                }
            }

            Column {
                Text("Plazo", fontWeight = FontWeight.Medium, color = NavyDark)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    plazos.forEachIndexed { i, p ->
                        FilterChip(
                            selected = plazoIndex == i,
                            onClick  = { plazoIndex = i },
                            label    = { Text("${p}m", fontSize = 12.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor     = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Column {
                Text("Tasa anual", fontWeight = FontWeight.Medium, color = NavyDark)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tasas.forEachIndexed { i, t ->
                        FilterChip(
                            selected = tasaIndex == i,
                            onClick  = { tasaIndex = i },
                            label    = { Text("${t.toInt()}%", fontSize = 12.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor     = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Resumen", fontWeight = FontWeight.SemiBold, color = NavyDark)
                    val totalPagar = cuota * plazos[plazoIndex]
                    val intereses  = totalPagar - monto
                    FilaResumen("Monto solicitado", "S/ %,.2f".format(monto))
                    FilaResumen("Total intereses",  "S/ %,.2f".format(intereses))
                    FilaResumen("Total a pagar",    "S/ %,.2f".format(totalPagar))
                }
            }

            Button(
                onClick  = { },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Text("Solicitar préstamo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FilaResumen(label: String, valor: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = GrayMedium, fontSize = 13.sp)
        Text(valor, fontWeight = FontWeight.Medium, color = NavyDark, fontSize = 13.sp)
    }
}

// ═══════════════════════════════════════════════════════════════
// M6 - HU-12: Meta de Ahorro
// ═══════════════════════════════════════════════════════════════
@Composable
fun AhorroScreen(onBack: () -> Unit) {
    val ahorro   = DemoData.cuentaAhorro
    val pct      = ahorro.porcentaje()
    var deposito by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MiBancoTopBar(titulo = "Cuenta de Ahorro", mostrarBack = true, onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape  = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(ahorro.nombre, color = GoldLight, fontWeight = FontWeight.SemiBold)
                        Text(ahorro.plazo, color = Color.White.copy(0.7f), fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ahorrado", color = Color.White.copy(0.7f), fontSize = 12.sp)
                            Text(
                                "S/ %,.2f".format(ahorro.saldo),
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 22.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Meta", color = Color.White.copy(0.7f), fontSize = 12.sp)
                            Text(
                                "S/ %,.2f".format(ahorro.meta),
                                color      = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 22.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress   = { pct },
                        modifier   = Modifier.fillMaxWidth().height(10.dp),
                        color      = GreenPositive,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text     = "%.1f%% completado".format(pct * 100),
                        color    = GoldLight,
                        fontSize = 12.sp
                    )
                }
            }

            Text("Abonar a la meta", fontWeight = FontWeight.SemiBold, color = NavyDark)

            OutlinedTextField(
                value         = deposito,
                onValueChange = { deposito = it },
                label         = { Text("Monto a depositar (S/)") },
                leadingIcon   = { Icon(Icons.Default.Savings, null, tint = NavyPrimary) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Button(
                onClick  = { },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled  = deposito.isNotBlank(),
                colors   = ButtonDefaults.buttonColors(containerColor = GreenPositive),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Depositar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}