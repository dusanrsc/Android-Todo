package com.infinitysoftware.todo

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

// Settings Section.
// CONSTANTS Section.
const val FILE_NAME = "todo.txt"

// Custom Made Functions Section.
// Write Data Into File Function.
fun writeData(items: SnapshotStateList<String>, context: Context) {

    // Creating Objects Of Classes Needed For Proper Writing Data Into File.
    val fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
    var objectOutputStream = ObjectOutputStream(fileOutputStream)
    val itemList = ArrayList<String>()

    // Calling Functions On Objects.
    itemList.addAll(items)
    objectOutputStream.writeObject(itemList)
    objectOutputStream.close()
}

// Read Data From File Function.
fun readData(context: Context): SnapshotStateList<String> {

    // Creating Objects Of Classes Needed For Proper Reading Data From File And Calling Functions On Objects.
    var itemList: ArrayList<String>

    // Try Catch Block For Catching Error While Program Starts For The First Time And There Are Not Created File For Storaging Data!
    try {
        val fileInputStream = context.openFileInput(FILE_NAME)
        val objecctInputStream = ObjectInputStream(fileInputStream)
        itemList = objecctInputStream.readObject() as ArrayList<String>
    } catch (e: FileNotFoundException) {
        itemList = ArrayList()
    }

    val items = SnapshotStateList<String>()
    items.addAll(itemList)

    return items
}