package tech.azurestar.kmp.ecommercecustomer.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import tech.azurestar.kmp.ecommercecustomer.R
import tech.azurestar.kmp.ecommercecustomer.language.TextProvider
import tech.azurestar.kmp.ecommercecustomer.ui.DefaultScaffold
import tech.azurestar.kmp.ecommercecustomer.ui.nav.NavLocations
import tech.azurestar.kmp.ecommercecustomer.vm.AuthViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.OrderViewModel

@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginAuthScreen(navController: NavController) {

    val authViewModel = koinInject<AuthViewModel>()
    val dbViewModel = koinInject<DBViewModel>()
    val orderViewModel = koinInject<OrderViewModel>()

    if (authViewModel.currentUser.collectAsStateWithLifecycle().value != null) {
        navController.navigate(NavLocations.PROFILE.name)
    }

    val state = LocalAuthState.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    DefaultScaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        }
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(300.dp)
                )
                AuthForm(state = state) {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedEmailField(value = email, onValueChange = { email = it })
                        OutlinedPasswordField(
                            value = password,
                            onValueChange = { password = it },
                            isError = password.isBlank()
                        )
                        Button(
                            onClick = {
                                authViewModel.signInWithEmail(
                                    email,
                                    password,
                                    {
                                        dbViewModel.initialize()
                                        orderViewModel.initialize()
                                    }) {
                                    println("yadsaaaaaa: $it")
                                    scope.launch { snackbarHostState.showSnackbar(it) }
                                }
                            }, //Login with email and password,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            enabled = state.validForm,
                        ) {
                            Text(TextProvider.LOGIN.getText())
                        }
                        TextButton(onClick = {
                            navController.navigate(NavLocations.AUTH_SIGN_UP.name) {
                                popUpTo(
                                    0
                                )
                            }
                        }) {
                            Text(TextProvider.SIGN_UP_INSTEAD.getText())
                        }
                    }
                }
            }
        }
    }
}