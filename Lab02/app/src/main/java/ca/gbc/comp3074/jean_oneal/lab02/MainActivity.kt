package ca.gbc.comp3074.jean_oneal.lab02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.jean_oneal.lab02.ui.theme.Lab02Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab02Theme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                    ActionButton(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun ActionButton(modifier: Modifier = Modifier){

    var counter = remember{ mutableIntStateOf(0) }
    var stepSize = remember{ mutableIntStateOf(1) }

    Column(modifier = modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
                ){

                Image(
                    painter = painterResource(id = R.drawable.lab02_comp3074),
                    contentDescription = "Application Logo from the black bulls",
                    modifier = Modifier.width(200.dp).height(100.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                Text("${counter.intValue}")

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                Button(
                    onClick ={
                        counter.intValue-= stepSize.intValue
                    },
                    modifier = Modifier.weight(1f)
                ){
                    Text(" - ")
                }
                Button(
                    onClick ={
                        counter.intValue+= stepSize.intValue
                    },
                    modifier = Modifier.weight(1f)
                ){
                    Text(" + ")
                }


            }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Button(
                onClick ={
                    counter.intValue = 0
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )

            ){
                Text(" Reset ")
            }
            Button(
                onClick ={
                    stepSize.intValue = if (stepSize.intValue == 1) 2 else 1
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                )

            ){
                Text(" Step ")
                Text(text = "Change Step (Current: ${stepSize.intValue})")
            }


        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab02Theme {
        ActionButton()
    }
}