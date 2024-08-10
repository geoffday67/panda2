package uk.co.sullenart.panda2

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import timber.log.Timber
import uk.co.sullenart.panda2.hobbyhouse.HobbyHouseViewModel
import uk.co.sullenart.panda2.kettle.KettleViewModel
import uk.co.sullenart.panda2.kitchen.KitchenViewModel
import uk.co.sullenart.panda2.photos.AlbumPager
import uk.co.sullenart.panda2.photos.PhotosViewModel
import uk.co.sullenart.panda2.service.Auth
import uk.co.sullenart.panda2.service.AuthInterceptor
import uk.co.sullenart.panda2.service.GooglePhotos
import uk.co.sullenart.panda2.service.TokensRepository
import uk.co.sullenart.panda2.shower.ShowerViewModel
import uk.co.sullenart.panda2.xmaslights.XmasLightsViewModel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree());
        }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                module {
                    single {
                        MqttManager(
                            server = MQTT_SERVER,
                            identifier = MQTT_ID,
                        )
                    }
                    singleOf(::SnackbarManager)
                    viewModelOf(::XmasLightsViewModel)
                    viewModelOf(::HobbyHouseViewModel)
                    viewModelOf(::KitchenViewModel)
                    viewModelOf(::KettleViewModel)
                    viewModelOf(::ShowerViewModel)
                    viewModelOf(::PhotosViewModel)

                    factoryOf(::AlbumPager)
                    singleOf(::GooglePhotos)
                    singleOf(::Auth)
                    singleOf(::AuthInterceptor)
                    factoryOf(::TokensRepository)
                    singleOf(::Settings)
                }
            )
        }

        val token = FirebaseMessaging.getInstance().getToken()
        Timber.d("Token = $token")
    }

    companion object {
        private const val MQTT_SERVER = "192.168.68.106"
        private const val MQTT_ID = "panda2"
    }
}