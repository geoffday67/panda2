package uk.co.sullenart.panda2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.glance.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import uk.co.sullenart.panda2.hobbyhouse.HobbyHouseScreen
import uk.co.sullenart.panda2.kettle.KettleScreen
import uk.co.sullenart.panda2.kitchen.KitchenScreen
import uk.co.sullenart.panda2.shower.ShowerScreen
import uk.co.sullenart.panda2.ui.theme.Panda2Theme

class MainActivity : ComponentActivity() {
    data class MenuItem(
        @StringRes val titleId: Int,
        val route: String,
    )

    private val kettleMenu = MenuItem(R.string.kettle_menu, "kettle")
    private val hobbyHouseMenu = MenuItem(R.string.hobby_house_menu, "hobby-house")
    private val kitchenMenu = MenuItem(R.string.kitchen_menu, "kitchen")
    private val showerMenu = MenuItem(R.string.shower_menu, "shower")
    private val menuItems = listOf(kitchenMenu, hobbyHouseMenu, kettleMenu, showerMenu)
    private val startItem = kitchenMenu

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            var currentItem: MenuItem by remember { mutableStateOf(startItem) }
            val snackbarHostState = remember { SnackbarHostState() }
            val snackbarManager: SnackbarManager = get()

            LaunchedEffect(Unit) {
                snackbarManager.messages.collect {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
            Panda2Theme {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet() {
                            menuItems.forEach {
                                NavigationDrawerItem(
                                    label = { Text(stringResource(it.titleId)) },
                                    selected = it == currentItem,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        currentItem = it
                                        navController.navigate(it.route)
                                    },
                                )
                            }
                        }
                    }
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            TopAppBar(
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            scope.launch { drawerState.open() }
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = null,
                                        )
                                    }
                                },
                                title = { Text(stringResource(R.string.app_title)) },
                                colors = TopAppBarDefaults.smallTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                            )
                        },
                    ) { padding ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(
                                    dimensionResource(R.dimen.margin),
                                )
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = startItem.route
                            ) {
                                composable(hobbyHouseMenu.route) {
                                    HobbyHouseScreen()
                                }
                                composable(kitchenMenu.route) {
                                    KitchenScreen()
                                }
                                composable(kettleMenu.route) {
                                    KettleScreen()
                                }
                                composable(showerMenu.route) {
                                    ShowerScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
