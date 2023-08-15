package uk.co.sullenart.panda2.photos

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.ShowTitle

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel = getViewModel()
) {
    val albumItems: LazyPagingItems<Album> = viewModel.albumItems.collectAsLazyPagingItems()
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
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ShowTitle("Photo Albums")

            Row(
                Modifier.fillMaxWidth(),
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

            if (albumItems.loadState.refresh == LoadState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                )
                {
                    items(
                        count = albumItems.itemCount,
                    ) { index ->
                        Album(albumItems[index])
                    }
                }
            }
        }
    }
}

@Composable
fun Album(
    album: Album?,
) {
    Column(
        Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (album == null) {
                Text("Waiting...")
                return@Row
            }
            Text("${album.title} (${album.items})")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (album == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color.LightGray)
                )
                return@Row
            }
            AsyncImage(
                model = album.coverUrl,
                contentDescription = null,
            )
        }
    }
}
