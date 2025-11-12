package com.example.focusparty

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(vm: BooksViewModel) {
    val books by vm.books.collectAsState()

    var editing by remember { mutableStateOf<Book?>(null) }   // null => ajout
    var askDelete by remember { mutableStateOf<Book?>(null) } // demande de confirmation

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mes Livres (double-clic)") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = Book(titre = "", auteur = "", annee = 0) }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 16.dp, top = padding.calculateTopPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(books, key = { it.id }) { book ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { /* noop */ },
                            onLongClick = { editing = book },         // Appui long => modifier
                            onDoubleClick = { askDelete = book }       // Double-clic => demander suppression
                        ),
                    elevation = CardDefaults.elevatedCardElevation(2.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(book.titre, style = MaterialTheme.typography.titleMedium)
                            Text(book.auteur, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text("${book.annee}", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }

    // Dialogue Ajout/Édition
    editing?.let { initial ->
        var titre by remember { mutableStateOf(initial.titre) }
        var auteur by remember { mutableStateOf(initial.auteur) }
        var annee by remember { mutableStateOf(if (initial.annee == 0) "" else initial.annee.toString()) }
        val isEdit = initial.id != 0L
        val ok = titre.isNotBlank() && auteur.isNotBlank() && annee.toIntOrNull() != null

        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text(if (isEdit) "Modifier le livre" else "Ajouter un livre") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(titre, { titre = it }, label = { Text("Titre") })
                    OutlinedTextField(auteur, { auteur = it }, label = { Text("Auteur") })
                    OutlinedTextField(
                        annee, { annee = it }, label = { Text("Année") },

                    )
                }
            },
            confirmButton = {
                TextButton(enabled = ok, onClick = {
                    val y = annee.toInt()
                    if (isEdit) vm.update(initial.copy(titre = titre.trim(), auteur = auteur.trim(), annee = y))
                    else vm.add(titre.trim(), auteur.trim(), y)
                    editing = null
                }) { Text(if (isEdit) "Enregistrer" else "Ajouter") }
            },
            dismissButton = { TextButton(onClick = { editing = null }) { Text("Annuler") } }
        )
    }

    // Confirmation suppression (déclenchée par double-clic)
    askDelete?.let { b ->
        AlertDialog(
            onDismissRequest = { askDelete = null },
            title = { Text("Supprimer ce livre ?") },
            text = { Text("${b.titre} — ${b.auteur} (${b.annee})") },
            confirmButton = {
                TextButton(onClick = { vm.delete(b); askDelete = null }) { Text("Supprimer") }
            },
            dismissButton = { TextButton(onClick = { askDelete = null }) { Text("Annuler") } }
        )
    }
}