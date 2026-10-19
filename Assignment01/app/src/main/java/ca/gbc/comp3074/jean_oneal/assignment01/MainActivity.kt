package ca.gbc.comp3074.jean_oneal.assignment01

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.jean_oneal.assignment01.ui.theme.Assignment01Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment01Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // 1. State variables to hold user input strings
    var hoursInput by remember { mutableStateOf("") }
    var rateInput by remember { mutableStateOf("") }
    var taxRateInput by remember { mutableStateOf("") }

    // 2. State variables to hold the calculated results
    var regularPay by remember { mutableStateOf(0.0) }
    var overtimePay by remember { mutableStateOf(0.0) }
    var totalPay by remember { mutableStateOf(0.0) }
    var taxAmount by remember { mutableStateOf(0.0) }

    // Boolean to track when to show the results (after the calculate button is pressed)
    var showResults by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Payroll Calculator",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 3. User Input Fields for Hours, Rate, and Tax
        OutlinedTextField(
            value = hoursInput,
            onValueChange = { hoursInput = it },
            label = { Text("Number of Hours") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = rateInput,
            onValueChange = { rateInput = it },
            label = { Text("Hourly Rate ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = taxRateInput,
            onValueChange = { taxRateInput = it },
            label = { Text("Tax Rate (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 4. Calculate Button logic
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Convert inputs to Double. If the field is empty, fallback to 0.0
                val hours = hoursInput.toDoubleOrNull() ?: 0.0
                val rate = rateInput.toDoubleOrNull() ?: 0.0
                val taxRate = taxRateInput.toDoubleOrNull() ?: 0.0


                if (hours <= 40) {
                    regularPay = hours * rate
                    overtimePay = 0.0
                } else {
                    regularPay = 40 * rate
                    overtimePay = (hours - 40) * (rate * 1.5)
                }

                totalPay = regularPay + overtimePay

                // Assuming tax rate is given as a percent (e.g., "13" for 13%), we divide by 100
                taxAmount = totalPay * (taxRate / 100)

                // Trigger UI update to display outputs
                showResults = true
            }
        ) {
            Text(text = "Calculate Pay")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Results display block
        if (showResults) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // String.format("%.2f") guarantees 2 decimal places for currency format
                Text(text = "Regular Pay: $${String.format("%.2f", regularPay)}")
                Text(text = "Overtime Pay: $${String.format("%.2f", overtimePay)}")
                Text(text = "Total Pay: $${String.format("%.2f", totalPay)}")
                Text(text = "Tax Amount: $${String.format("%.2f", taxAmount)}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Navigation Button to About Activity
        Button(onClick = {
            val intent = Intent(context, AboutActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "About")
        }
    }
}