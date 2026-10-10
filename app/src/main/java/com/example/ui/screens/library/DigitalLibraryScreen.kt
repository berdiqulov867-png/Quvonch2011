package com.example.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LibraryResourceEntity
import com.example.data.model.UserRole
import com.example.ui.BilimViewModel
import com.example.ui.components.BilimCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyInputBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import java.util.UUID

@Composable
fun DigitalLibraryScreen(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val libraryResources by viewModel.libraryResources.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectFilter by remember { mutableStateOf("BARCHASI") }
    var selectedCategoryFilter by remember { mutableStateOf("BARCHASI") }

    var viewingBook by remember { mutableStateOf<LibraryResourceEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val canUpload = currentUser?.role == UserRole.ADMIN.name || currentUser?.role == UserRole.TEACHER.name

    val filteredResources = libraryResources.filter { res ->
        val matchSubj = if (selectedSubjectFilter == "BARCHASI") true else res.subjectId == selectedSubjectFilter
        val matchCat = if (selectedCategoryFilter == "BARCHASI") true else res.category == selectedCategoryFilter
        val matchSearch = res.title.contains(searchQuery, ignoreCase = true) ||
                res.author.contains(searchQuery, ignoreCase = true) ||
                res.description.contains(searchQuery, ignoreCase = true)
        matchSubj && matchCat && matchSearch
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Elektron Kutubxona",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "Tarix va Geografiya fanlaridan darsliklar, atlaslar va manbalar",
                fontSize = 12.sp,
                color = TextMutedGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Darslik, atlas yoki muallif bo'yicha qidirish...", color = TextMutedGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldHistory) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextMutedGray)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldHistory,
                    unfocusedBorderColor = DarkNavyCardBorder,
                    focusedContainerColor = DarkNavyInputBg,
                    unfocusedContainerColor = DarkNavyInputBg,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subject Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedSubjectFilter == "BARCHASI",
                        onClick = { selectedSubjectFilter = "BARCHASI" },
                        label = { Text("Barcha fanlar") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedSubjectFilter == "tarix",
                        onClick = { selectedSubjectFilter = "tarix" },
                        label = { Text("Tarix (Oltin)") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldHistory, selectedLabelColor = Color.Black)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedSubjectFilter == "geografiya",
                        onClick = { selectedSubjectFilter = "geografiya" },
                        label = { Text("Geografiya (Moviy)") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BlueGeography, selectedLabelColor = Color.Black)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resources Grid/List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredResources) { book ->
                    val isTarix = book.subjectId == "tarix"
                    val accentColor = if (isTarix) GoldHistory else BlueGeography

                    BilimCard(onClick = { viewingBook = book }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(accentColor.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SubjectBadge(subjectId = book.subjectId)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = DarkNavyInputBg
                                    ) {
                                        Text(
                                            text = book.category,
                                            fontSize = 10.sp,
                                            color = TextMutedGray,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = book.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "${book.author} • ${book.pageCount} bet • ${book.fileSize}",
                                    fontSize = 11.sp,
                                    color = TextMutedGray
                                )
                            }

                            Button(
                                onClick = { viewingBook = book },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentColor.copy(alpha = 0.2f),
                                    contentColor = accentColor
                                )
                            ) {
                                Text("O'qish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        if (canUpload) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = GoldHistory,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Kitob qo'shish")
            }
        }
    }

    // Interactive Reader / Study Viewer Modal
    viewingBook?.let { book ->
        val accentColor = if (book.subjectId == "tarix") GoldHistory else BlueGeography
        AlertDialog(
            onDismissRequest = { viewingBook = null },
            title = {
                Text(text = book.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.height(350.dp)) {
                    Text(text = "Muallif: ${book.author}", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Hajmi: ${book.pageCount} bet (${book.fileSize}) • Yuklagan: ${book.uploaderName}", color = TextMutedGray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = book.description, color = TextWhite, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Mundarija va boblar sharhi:", color = TextMutedGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkNavyInputBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkNavyCardBorder),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        LazyColumn(modifier = Modifier.padding(10.dp)) {
                            item {
                                Text(
                                    text = book.chaptersJson,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingBook = null },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                ) {
                    Text("O'qish rejimini yopish")
                }
            },
            containerColor = DarkNavySurface
        )
    }

    // Add Library Resource Dialog
    if (showAddDialog) {
        AddResourceDialog(
            uploaderName = currentUser?.fullName ?: "Ustoz",
            onDismiss = { showAddDialog = false },
            onConfirm = { res ->
                viewModel.addLibraryResource(res)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddResourceDialog(
    uploaderName: String,
    onDismiss: () -> Unit,
    onConfirm: (LibraryResourceEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var subjectId by remember { mutableStateOf("tarix") }
    var category by remember { mutableStateOf("Darslik") }
    var description by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("180") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yangi material yuklash", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Kitob yoki qo'llanma nomi", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Muallif(lar)", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = subjectId == "tarix",
                        onClick = { subjectId = "tarix" },
                        label = { Text("Tarix") }
                    )
                    FilterChip(
                        selected = subjectId == "geografiya",
                        onClick = { subjectId = "geografiya" },
                        label = { Text("Geografiya") }
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Qisqacha tavsif", color = TextMutedGray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                OutlinedTextField(
                    value = pageCount,
                    onValueChange = { pageCount = it },
                    label = { Text("Sahifalar soni", color = TextMutedGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val res = LibraryResourceEntity(
                        id = "lib_${UUID.randomUUID().toString().take(8)}",
                        subjectId = subjectId,
                        title = title,
                        author = author,
                        category = category,
                        description = description,
                        fileSize = "15.2 MB",
                        pageCount = pageCount.toIntOrNull() ?: 100,
                        chaptersJson = "1-Bob: Kirish va asosiy tushunchalar\n2-Bob: Amaliy mavzular va xaritalar",
                        uploaderName = uploaderName
                    )
                    onConfirm(res)
                },
                enabled = title.isNotBlank() && author.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldHistory, contentColor = Color.Black)
            ) {
                Text("Yuklash")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Bekor qilish", color = TextMutedGray) }
        },
        containerColor = DarkNavySurface
    )
}
