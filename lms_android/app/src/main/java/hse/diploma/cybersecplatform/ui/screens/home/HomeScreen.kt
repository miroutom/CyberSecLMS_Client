package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.ui.components.SearchBar
import hse.diploma.cybersecplatform.ui.components.systemBars.CustomNavigationBar
import hse.diploma.cybersecplatform.ui.components.systemBars.TopBar
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopBar(navController)
        },
        bottomBar = {
            CustomNavigationBar(navController)
        },
        content = { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
            ) {
                SearchBar(
                    searchQuery = "",
                    onSearchQueryChange = {},
                )
                ScrollableContent()
            }
        },
    )
}

@Composable
fun ScrollableContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(100) { index ->
            Text(
                text = "Item $index",
                style = Typography.bodyMedium,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CyberSecPlatformTheme {
        HomeScreen(navController = rememberNavController())
    }
}
