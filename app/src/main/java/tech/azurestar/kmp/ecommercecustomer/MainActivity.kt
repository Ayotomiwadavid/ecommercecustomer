package tech.azurestar.kmp.ecommercecustomer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.coil.coil3
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import tech.azurestar.kmp.ecommercecustomer.di.appModule
import tech.azurestar.kmp.ecommercecustomer.ui.nav.Navigation
import tech.azurestar.kmp.ecommercecustomer.ui.theme.EcommerceCustomerTheme
import tech.azurestar.kmp.ecommercecustomer.vm.AuthViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.OrderViewModel

class MainActivity : ComponentActivity() {
    @OptIn(SupabaseExperimental::class, ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        setContent {

            KoinContext(
                context = koinApplication {
                    androidContext(this@MainActivity)
                    modules(appModule)
                }.koin
            ) {

                val authViewModel = koinInject<AuthViewModel>()
                val dbViewModel = koinInject<DBViewModel>()
                val orderViewModel = koinInject<OrderViewModel>()
                val authViewModelInitialized = authViewModel.isInitialized.collectAsState().value

                LaunchedEffect(key1 = authViewModelInitialized) {
                    if (authViewModelInitialized) {
                        dbViewModel.initialize()
                        orderViewModel.initialize()
                    }
                }

                val supabaseClient = koinInject<SupabaseClient>()
                setSingletonImageLoaderFactory { platformContext ->
                    ImageLoader.Builder(platformContext)
                        .components {
                            add(supabaseClient.coil3)
                            //Your network fetcher factory
                            add(KtorNetworkFetcherFactory())
                        }
                        .build()
                }

                if (dbViewModel.initialized.collectAsState().value)
                    EcommerceCustomerTheme {
                        Navigation()
                    }
            }
        }
    }
}
