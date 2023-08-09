package uk.co.sullenart.panda2.photos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.launch
import uk.co.sullenart.panda2.service.Auth

class PhotosViewModel(
    photoPager: PhotoPager,
    private val auth: Auth,
) : ViewModel() {
    val photoItems = photoPager.flowPhotos.cachedIn(viewModelScope)
    var authenticated by mutableStateOf(false)

    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestServerAuthCode(Auth.CLIENT_ID, true)
        .requestScopes(Scope(SCOPE))
        .build()

    fun completeAuth(account: GoogleSignInAccount) {
        viewModelScope.launch {
            val code = account.serverAuthCode ?: ""
            auth.exchangeCode(code)
        }
    }

    companion object {
        private const val SCOPE = "https://www.googleapis.com/auth/photoslibrary.readonly"
    }
}