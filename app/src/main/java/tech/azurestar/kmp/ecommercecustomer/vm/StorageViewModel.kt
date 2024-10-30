package tech.azurestar.kmp.ecommercecustomer.vm

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

const val IMAGES = "images"
private const val PROFILE = "profile"

class StorageViewModel(private val client: SupabaseClient, private val applicationContext: Context): ViewModel() {

    private val storage = client.storage
    private val images = storage["images"]

    @OptIn(ExperimentalUuidApi::class)
    fun uploadProfileImage(imageUri: Uri, callback: (String) -> Unit) {
        viewModelScope.launch {
            val id = client.auth.currentUserOrNull()!!.id
            callback("$id/" + images.upload("$id/$PROFILE${Uuid.random()}${getFileType(applicationContext, imageUri)?.replace("/", ".")}", imageUri).path)
        }
    }

    fun getFileType(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver

        // Try to get MIME type directly from ContentResolver
        var mimeType = contentResolver.getType(uri)

        if (mimeType == null) {
            // If MIME type is not available, try to get it from the file extension
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }

        return mimeType
    }

    fun removeOldImage(oldImage: String) {
        viewModelScope.launch {
            images.delete(oldImage)
        }
    }
}