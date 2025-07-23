package com.infinitysoftware.todo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.infinitysoftware.todo.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoTheme {
                Scaffold { innerPadding ->
                    mainPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainPage(defaultFontSize: TextUnit = 16.sp, maxCharLength: Int = 23, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val todoList = readData(context)
    val todo = remember { mutableStateOf("") }
    val deleteTodoStatus = remember { mutableStateOf(false) }
    val clickedItemIndex = remember { mutableStateOf(0) }
    val editTodoStatus = remember { mutableStateOf(false) }
    val clickedItem = remember { mutableStateOf("") }
    val textDialogStatus = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.wood_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )

        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .weight(8f)
                        .height(60.dp),
                    value = todo.value,
                    onValueChange = { todo.value = it },
                    label = { Text(text = "Enter Your Todo Here!") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = fontDefaultColor,
                        unfocusedTextColor = fontDefaultColor,
                        unfocusedContainerColor = textFieldDefaultBackgroundColor,
                        focusedContainerColor = textFieldDefaultBackgroundColor,
                        focusedTextColor = fontDefaultColor,
                        unfocusedLabelColor = fontDefaultColor,
                        cursorColor = fontDefaultColor
                    ),
                    shape = RoundedCornerShape(100.dp, 0.dp, 0.dp, 100.dp),
                    textStyle = TextStyle(textAlign = TextAlign.Center)
                )

                Button(
                    modifier = Modifier
                        .weight(2f)
                        .height(60.dp),
                    shape = RoundedCornerShape(0.dp, 100.dp, 100.dp, 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = saveButtonDefaultContainerColor,
                        contentColor = fontDefaultColor
                    ),
                    onClick = {
                        if (todo.value.isNotEmpty()) {
                            todoList.add(todo.value)
                            writeData(todoList, context)
                            todo.value = ""
                            focusManager.clearFocus()
                        } else {
                            Toast.makeText(context, "Please Enter Your Todo First!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }

            LazyColumn {
                items(
                    count = todoList.size,
                    itemContent = { index ->
                        val item = todoList[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            shape = RoundedCornerShape(5.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardDefaultContainerColor,
                                contentColor = fontDefaultColor,
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .clickable {
                                            clickedItem.value = item
                                            textDialogStatus.value = true
                                        },
                                    text = if (item.length > maxCharLength) item.take(maxCharLength) + "..." else item,
                                    color = fontDefaultColor,
                                    fontSize = defaultFontSize,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row {
                                    IconButton(onClick = {
                                        editTodoStatus.value = true
                                        clickedItemIndex.value = index
                                        clickedItem.value = item
                                    }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = {
                                        deleteTodoStatus.value = true
                                        clickedItemIndex.value = index
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    if (deleteTodoStatus.value) {
        AlertDialog(
            onDismissRequest = { deleteTodoStatus.value = false },
            title = { Text(text = "Delete") },
            text = { Text(text = "Do you want to delete this Todo item?") },
            confirmButton = {
                TextButton(onClick = {
                    todoList.removeAt(clickedItemIndex.value)
                    writeData(todoList, context)
                    deleteTodoStatus.value = false
                    Toast.makeText(context, "Todo item has been successfully deleted!", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "YES")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTodoStatus.value = false }) {
                    Text(text = "NO")
                }
            }
        )
    }

    if (editTodoStatus.value) {
        AlertDialog(
            onDismissRequest = { editTodoStatus.value = false },
            title = { Text(text = "Update") },
            text = {
                TextField(
                    value = clickedItem.value,
                    onValueChange = { clickedItem.value = it }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    todoList[clickedItemIndex.value] = clickedItem.value
                    writeData(todoList, context)
                    editTodoStatus.value = false
                    Toast.makeText(context, "Todo item has been successfully updated!", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "YES")
                }
            },
            dismissButton = {
                TextButton(onClick = { editTodoStatus.value = false }) {
                    Text(text = "NO")
                }
            }
        )
    }

    if (textDialogStatus.value) {
        AlertDialog(
            onDismissRequest = { textDialogStatus.value = false },
            title = { Text(text = "Todo Item") },
            text = { Text(text = clickedItem.value) },
            confirmButton = {
                TextButton(onClick = { textDialogStatus.value = false }) {
                    Text(text = "OK")
                }
            },
        )
    }
}
