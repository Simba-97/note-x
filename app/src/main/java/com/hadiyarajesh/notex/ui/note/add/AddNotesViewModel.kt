package com.hadiyarajesh.notex.ui.note.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadiyarajesh.notex.database.dao.NoteDao
import com.hadiyarajesh.notex.database.entity.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddNotesViewModel @Inject constructor(private val noteDao: NoteDao) : ViewModel() {
    fun saveNote(noteState: NoteState, noteId: Long?) {
        viewModelScope.launch {
            noteDao.insertOrUpdate(
                note = Note(
                    noteId = noteId,
                    title = noteState.title.value,
                    content = noteState.noteDesc.value,
                    archived = false,
                    color = convertIntColorToHex(noteState.color.value),
                    createdOn = Instant.now(),
                    updatedOn = Instant.now()
                )
            )

        }
    }

    suspend fun getNote(noteId: Long): Note {
        val note = viewModelScope.async(Dispatchers.IO){
            noteDao.getById(noteId)
        }
        return note.await()
    }

    private fun convertIntColorToHex(color: Int) : String{
        return String.format("#%06X", 0xFFFFFF and color)
    }

    sealed class UiEvent {
        data class ShowToast(val message: String): UiEvent()
        object SaveNote: UiEvent()
    }
}