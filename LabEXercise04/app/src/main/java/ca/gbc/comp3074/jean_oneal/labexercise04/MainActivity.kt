package ca.gbc.comp3074.jean_oneal.labexercise04

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.jean_oneal.labexercise04.ui.theme.LabEXercise04Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            LabEXercise04Theme {
                val viewModel = CustomerViewModelFactory(database.customerDao()).create(
                    CustomerViewModel::class.java
                )
                CustomerApp(viewModel)
            }
        }
    }
}

@Composable
fun CustomerApp(viewModel: CustomerViewModel) {
    val searchResults = viewModel.searchResults.collectAsState(initial = emptyList())
    val searchQuery = viewModel.searchQuery.collectAsState()

    CustomerScreen(
        customers = searchResults.value,
        searchQuery = searchQuery.value,
        onSearchQueryChange = viewModel::search,
        onSearchClick = { viewModel.search(searchQuery.value) },
        onClearSearchClick = viewModel::clearSearch,
        onAddNewCustomer = viewModel::addCustomer,
        onDeleteCustomer = viewModel::deleteCustomer
    )
}

@Composable
fun CustomerScreen(
    customers: List<Customer>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onClearSearchClick: () -> Unit,
    onAddNewCustomer: (String, Int, Boolean) -> Unit,
    onDeleteCustomer: (Customer) -> Unit
) {
    val name = remember { mutableStateOf("") }
    val ageText = remember { mutableStateOf("") }
    val isActive = remember { mutableStateOf(false) }
    val message = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "LabExercise04",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = ageText.value,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    ageText.value = newValue
                }
            },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Active")
            Spacer(Modifier.weight(1f))
            Switch(
                checked = isActive.value,
                onCheckedChange = { isActive.value = it }
            )
        }
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val nameVal = name.value
                val age = ageText.value.toIntOrNull()
                if (nameVal.isBlank()) {
                    message.value = "Name cannot be blank"
                } else if (age == null) {
                    message.value = "Age must be a number"
                } else {
                    message.value = null
                    onAddNewCustomer(nameVal.trim(), age, isActive.value)
                    name.value = ""
                    ageText.value = ""
                    isActive.value = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add Customer")
        }

        message.value?.let { msg ->
            Spacer(Modifier.height(4.dp))
            Text(text = msg, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSearchClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Search")
            }
            Button(
                onClick = onClearSearchClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Clear")
            }
        }

        Spacer(Modifier.height(12.dp))

        CustomerList(
            customers = customers,
            onDeleteCustomer = onDeleteCustomer,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
fun CustomerList(
    customers: List<Customer>,
    onDeleteCustomer: (Customer) -> Unit,
    modifier: Modifier = Modifier
) {
    if (customers.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No customers found")
        }
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = customers,
                key = { customer -> customer.id }
            ) { customer ->
                CustomerItem(
                    customer = customer,
                    onDeleteCustomer = onDeleteCustomer
                )
            }
        }
    }
}

@Composable
fun CustomerItem(
    customer: Customer,
    onDeleteCustomer: (Customer) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "${customer.name} (Age: ${customer.age})")
                Text(text = if (customer.isActive) "Active" else "Inactive")
            }
            Button(onClick = { onDeleteCustomer(customer) }) {
                Text(text = "Delete")
            }
        }
    }
}
