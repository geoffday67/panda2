package uk.co.sullenart.panda2.photos

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.ShowTitle

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel = getViewModel()
) {
    val photoItems: LazyPagingItems<Photo> = viewModel.photoItems.collectAsLazyPagingItems()
    val authenticated = viewModel.authenticated.collectAsState(initial = false).value
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // TODO Check for error
        val account: GoogleSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(it.data).result
        viewModel.completeAuth(account)
    }
    val context = LocalContext.current
    val client = remember { GoogleSignIn.getClient(context, viewModel.signInOptions) }

    Box(
        Modifier.fillMaxSize()
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ){
            ShowTitle("Photo Albums")

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                if (authenticated) {
                    Button(
                        onClick = {
                            client.signOut()
                            viewModel.signOut()
                        }) {
                        Text("Sign out")
                    }
                } else {
                    Button(
                        onClick = {
                            launcher.launch(client.signInIntent)
                        }) {
                        Text("Sign in")
                    }
                    return
                }
            }

            LazyColumn(
            ) {
                items(
                    count = photoItems.itemCount,
                ) { index ->
                    Text(photoItems[index]?.title ?: "Unknown")
                }
            }
        }
    }
}
