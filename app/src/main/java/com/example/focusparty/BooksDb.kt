package com.example.focusparty

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "books.db"
private const val DB_VERSION = 1
private const val TABLE = "books"

class BooksDb(context: Context) : SQLiteOpenHelper(context, DB_NAME,
                                        null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titre TEXT NOT NULL,
                auteur TEXT NOT NULL,
                annee INTEGER NOT NULL
            )
            """.trimIndent()
        )
        // Option: seed
        // db.execSQL("INSERT INTO $TABLE(titre,auteur,annee) VALUES('Exemple','Auteur',2024)")
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        // migrations si besoin
    }

    fun insert(book: Book): Long {
        val cv = ContentValues().apply {
            put("titre", book.titre)
            put("auteur", book.auteur)
            put("annee", book.annee)
        }
        return writableDatabase.insert(TABLE,
            null, cv)
    }

    fun update(book: Book) {
        val cv = ContentValues().apply {
            put("titre", book.titre)
            put("auteur", book.auteur)
            put("annee", book.annee)
        }
        writableDatabase.update(
            TABLE, cv, "id=?",
            arrayOf(book.id.toString()))
    }

    fun delete(id: Long) {
        writableDatabase.delete(TABLE,
            "id=?", arrayOf(id.toString()))
    }

    fun getAll(): List<Book> {
        val out = mutableListOf<Book>()
        readableDatabase.rawQuery(
            "SELECT id,titre,auteur,annee FROM $TABLE" +
                    " ORDER BY id DESC", null
        ).use { c ->
            while (c.moveToNext()) {
                out += Book(
                    id = c.getLong(0),
                    titre = c.getString(1),
                    auteur = c.getString(2),
                    annee = c.getInt(3)
                )
            }
        }
        return out
    }
}