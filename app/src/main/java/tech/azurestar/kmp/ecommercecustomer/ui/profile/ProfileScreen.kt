package tech.azurestar.kmp.ecommercecustomer.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import io.github.jan.supabase.storage.authenticatedStorageItem
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.R
import tech.azurestar.kmp.ecommercecustomer.db.item.Customer
import tech.azurestar.kmp.ecommercecustomer.language.TextProvider
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultBottomNavBar
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.IMAGES

@Composable
fun ProfileScreen(navController: NavController) {

    val dbViewModel = koinInject<DBViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val customerAccount = dbViewModel.customerAccount.collectAsState().value

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }
    var text by remember {
        mutableStateOf(customerAccount?.name ?: "")
    }

    DefaultScaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if(customerAccount != null) {
                DefaultBottomNavBar(navController = navController)
            }
        }
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PhotoSelectorView(selectedImage, customerAccount) {
                selectedImage = it
            }
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                leadingIcon = {
                    Image(
                        imageVector = Icons.Default.Face,
                        contentDescription = null
                    )
                },
                label = { Text(TextProvider.NAME.getText()) },
                isError = text.isBlank()
            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(onClick = {
                if (text.isBlank()) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(TextProvider.PLEASE_FILL_OUT_ALL_FIELDS.getText())
                    }
                } else {
                    if(customerAccount == null)
                        dbViewModel.makeCustomerAccount(text, selectedImage)
                    else
                        dbViewModel.updateCustomerAccount(text, selectedImage) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(TextProvider.ACCOUNT_UPDATED.getText())
                            }
                        }
                }
            }) {
                Text(text = if(customerAccount == null) TextProvider.CREATE_ACCOUNT.getText() else TextProvider.UPDATE_ACCOUNT.getText())
            }
        }
    }
}

@Composable
fun PhotoSelectorView(selectedImage: Uri?, customer: Customer?, onImageSelected: (Uri?) -> Unit) {

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onImageSelected
    )

    fun launchPhotoPicker() {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }


    if (selectedImage != null) {
        AsyncImage(
            model = selectedImage,
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .clickable { launchPhotoPicker() },
            contentScale = ContentScale.Crop
        )
    } else if(customer?.profileImage != null) {
        AsyncImage(
            model = authenticatedStorageItem(IMAGES, customer.profileImage),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .clickable { launchPhotoPicker() },
            contentScale = ContentScale.Crop
        )
    }
    else {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(1.dp, Color.Gray, CircleShape), contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = R.drawable.profile_placeholder,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .clickable { launchPhotoPicker() },
                contentScale = ContentScale.Fit
            )
        }
    }
}
