package com.example.alwayson.utils

import android.content.Context
import java.io.File
import java.io.IOException


class Utils {
    companion object {
        fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        fun deleteCache(context: Context) {
            try {
                val cacheDir:File=context.cacheDir
                deleteDir(cacheDir)
            } catch (e: Exception) {
            }
        }

        private fun deleteDir(dir: File?): Boolean {
            return if (dir != null && dir.isDirectory) {
                val children: Array<String> = arrayOf(dir.toString())
                for (i in children.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
                dir.delete()
            } else if (dir != null && dir.isFile) {
                dir.delete()
            } else {
                false
            }
        }
    }



}