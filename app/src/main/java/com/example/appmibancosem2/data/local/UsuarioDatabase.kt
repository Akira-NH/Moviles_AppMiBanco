package com.example.appmibancosem2.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UsuarioDatabase(context: Context) :
    SQLiteOpenHelper(context, "mibanco.db", null, 3) { // subir versión

    companion object {
        const val TABLA = "usuarios"
        const val COL_ID = "id"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {

        // ⚠️ IMPORTANTE: no borres otras tablas, solo crea esta
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLA (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EMAIL TEXT UNIQUE,
                $COL_PASSWORD TEXT
            )
        """.trimIndent())

        // 👇 Usuario de prueba
        db.execSQL("""
            INSERT INTO $TABLA ($COL_EMAIL, $COL_PASSWORD)
            VALUES ('admin@demo.com', '1234')
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Solo crear si no existe (NO borrar todo)
        onCreate(db)
    }

    // 🔐 LOGIN
    fun validarUsuario(email: String, password: String): Boolean {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLA WHERE $COL_EMAIL=? AND $COL_PASSWORD=?",
            arrayOf(email, password)
        )

        val existe = cursor.count > 0

        cursor.close()
        db.close()

        return existe
    }

    // 📝 REGISTRO (opcional)
    fun registrar(email: String, password: String): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COL_EMAIL, email)
            put(COL_PASSWORD, password)
        }

        val res = db.insert(TABLA, null, values)
        db.close()

        return res != -1L
    }
}