package ca.gbc.comp3074.jean_oneal.labex3

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageItem(message: Message){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Avatar with red border
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            border = BorderStroke(2.dp, Color.Red),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Red
                )
            }
        }
        
        Spacer(modifier = Modifier.size(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = message.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Message bubble
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF3EFEF)
            ) {
                Text(
                    text = message.message,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = Color.Black
                )
            }
        }
    }
}


@Composable
fun SimpleMessageList(messages: List<Message>){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ){
        messages.forEach { message ->
            MessageItem(message)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}



@Composable
fun MessageScreen(modifier: Modifier){
    val context = LocalContext.current
    val messages = remember { loadUser(context) }

    SimpleMessageList(messages)
}