package hse.diploma.cybersecplatform.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.components.SearchBar
import hse.diploma.cybersecplatform.ui.components.cards.VulnerabilityCard
import hse.diploma.cybersecplatform.ui.components.systemBars.CustomNavigationBar
import hse.diploma.cybersecplatform.ui.components.systemBars.TopBar
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.utils.mockVulnerabilityItems

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
                    modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color.White),
            ) {
                SearchBar(
                    searchQuery = "",
                    onSearchQueryChange = {},
                    modifier = Modifier.background(Color.White),
                )
                // TODO: replace with real data
                ScrollableContent(mockVulnerabilityItems)
            }
        },
    )
}

@Composable
fun ScrollableContent(items: List<Pair<VulnerabilityType, Int>>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    ) {
        items.forEach { item ->
            item {
                VulnerabilityCard(
                    type = item.first,
                    tasksCount = item.second,
                    onClick = { },
                )
            }
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
