package com.example.lab7

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

@Composable
fun ScreenUser() {
    val context = LocalContext.current
    var db: UserDatabase

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser by remember { mutableStateOf("") }

    db = crearDatabase(context)
    val dao = db.userDao()

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(50.dp))
        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name:") },
            singleLine = true
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name:") },
            singleLine = true
        )
        Button(
            onClick = {
                val usuario = User(0, firstName, lastName) // Aquí la clase user en minúsculas
                coroutineScope.launch {
                    AgregarUsuario(usuario, dao)
                }
                firstName = ""
                lastName = ""
            }
        ) {
            Text("Agregar Usuario", fontSize = 16.sp)
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    val data = getUsers(dao)
                    dataUser = data
                }
            }
        ) {
            Text("Listar Usuarios", fontSize = 16.sp)
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    dao.deleteLastUser() // elimina ultimo usuario
                    val data = getUsers(dao = dao) // con esto actualizamos lista
                    dataUser = data
                }
            }
        ) {
            Text("Eliminar Último Usuario", fontSize = 16.sp)
        }
        Text(
            text = dataUser, fontSize = 20.sp
        )
    }
}

@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao: UserDao): String {
    var rpta = ""
    val users = dao.getAll()
    users.forEach { user ->
        val fila = "${user.firstName} - ${user.lastName}\n"
        rpta += fila
    }
    return rpta
}

suspend fun AgregarUsuario(usuario: User, dao: UserDao) { // "user" en minúscula
    try {
        dao.insert(usuario)
    } catch (e: Exception) {
        Log.e("User", "Error: insert: ${e.message}")
    }
}
