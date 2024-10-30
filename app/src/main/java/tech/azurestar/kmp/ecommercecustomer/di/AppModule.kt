package tech.azurestar.kmp.ecommercecustomer.di

import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import tech.azurestar.kmp.ecommercecustomer.SUPABASE_KEY
import tech.azurestar.kmp.ecommercecustomer.SUPABASE_URL
import tech.azurestar.kmp.ecommercecustomer.vm.DBViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.AuthViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.CheckoutViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.OrderViewModel
import tech.azurestar.kmp.ecommercecustomer.vm.StorageViewModel

@OptIn(ExperimentalSerializationApi::class)
val appModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            defaultSerializer = KotlinXSerializer( Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
            })
            install(Postgrest)
            install(Auth)
            install(Storage)
            install(Coil3Integration)
            install(Realtime)
        }
    }
    single <DBViewModel> { DBViewModel(get(), get()) }
    single<AuthViewModel> { AuthViewModel(get()) }
    single { StorageViewModel(get(), androidContext()) }
    single { OrderViewModel(get()) }
    single { CheckoutViewModel() }
}