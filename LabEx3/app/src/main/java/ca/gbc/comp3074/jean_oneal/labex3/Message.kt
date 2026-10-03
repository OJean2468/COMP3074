package ca.gbc.comp3074.jean_oneal.labex3

import android.content.Context

data class Message(
    val name: String,
    val message: String,
)

fun loadUser(context: Context): List<Message>{

    val name = context.resources.getStringArray(R.array.User_name)
    val message = context.resources.getStringArray(R.array.user_message)
//n -> represents the name
// m -> represents the message
    return name.zip(message){ n, m ->
        Message(n, m)
    }
}
