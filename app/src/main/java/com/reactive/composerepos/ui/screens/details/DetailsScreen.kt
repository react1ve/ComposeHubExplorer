package com.reactive.composerepos.ui.screens.details

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.reactive.composerepos.R
import com.reactive.composerepos.ui.composables.LoadingScreen
import com.reactive.composerepos.ui.composables.NavigateUp
import com.reactive.composerepos.ui.composables.NetworkErrorScreen
import com.reactive.composerepos.ui.composables.SmallIconWithAmount
import com.reactive.composerepos.ui.composables.UnknownErrorScreen
import com.reactive.composerepos.ui.screens.ScreenStatus
import com.reactive.composerepos.ui.theme.KotlinRepositoriesTheme
import com.reactive.composerepos.ui.theme.Link
import com.reactive.domain.model.Repo
import com.reactive.domain.model.User

@Composable
fun DetailsScreen(navigateUp : () -> Unit) {
    Surface {
        DetailsScreenRoute(hiltViewModel<DetailsViewModel>(), navigateUp)
    }
}

@Composable
private fun DetailsScreenRoute(viewModel : DetailsViewModel, navigateUp : () -> Unit) {
    val state by viewModel.state.collectAsState()
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("") },
            navigationIcon = { NavigateUp(onClick = navigateUp) }
        )
    }) { _: PaddingValues ->
        when (state.screenStatus) {
            ScreenStatus.NO_INTERNET -> NetworkErrorScreen()
            ScreenStatus.ERROR -> UnknownErrorScreen()
            ScreenStatus.LOADING -> LoadingScreen(modifier = Modifier.fillMaxSize())
            ScreenStatus.SUCCESS -> DetailsScreenContent(state.repo)
        }
    }
}

@Composable
private fun DetailsScreenContent(repo : Repo?) {
    val uriHandler = LocalUriHandler.current
    if (repo != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OwnerImage(repo)

            Spacer(modifier = Modifier.size(32.dp))
            Text(
                text = repo.fullName,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(16.dp))

            StarsAndForks(repo)

            Spacer(modifier = Modifier.size(16.dp))
            DescriptionItem(R.string.home_page, repo.homepage)

            Spacer(modifier = Modifier.size(16.dp))

            DescriptionItem(R.string.repository, repo.url, true) { data ->
                uriHandler.openUri(data)
            }

            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = R.string.description),
                style = MaterialTheme.typography.subtitle1,
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = repo.description,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OwnerImage(repo : Repo) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(repo.owner.avatarUrl)
            .crossfade(true)
            .build(),
        contentDescription = repo.owner.login,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(CircleShape)
            .size(256.dp)
    )
}

@Composable
private fun StarsAndForks(repo : Repo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = SpaceEvenly
    ) {
        SmallIconWithAmount(
            painter = rememberVectorPainter(image = Icons.Default.Star),
            text = repo.stars.toString()
        )
        SmallIconWithAmount(
            painter = painterResource(id = R.drawable.ic_fork),
            text = repo.forks.toString()
        )
    }
}

@Composable
private fun DescriptionItem(
    title : Int,
    content : String,
    isLink : Boolean = false,
    onClick : (data : String) -> Unit = {},
) {
    if (content.isNotEmpty()) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            color = if (isLink) Link else MaterialTheme.colors.onSurface,
            text = content,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (isLink) onClick(content) }
        )
    }
}

@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Day Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun Preview() {
    KotlinRepositoriesTheme() {
        Surface {
            DetailsScreenContent(
                Repo(
                    id = 1,
                    name = "kotlinReallyReallyReallyLargeName",
                    fullName = "jetbrains/kotlinReallyReallyReallyLargeName",
                    owner = User(
                        login = "jetbrains",
                        avatarUrl = "https://avatars.githubusercontent.com/u/878437?v=4"
                    ),
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                        "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
                        "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                        "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla " +
                        "pariatur.",
                    url = "https://github.com/JetBrains/kotlasdasdasdadasin",
                    homepage = "https://kotladasdasdasdasdasdasdasinlang.org",
                    stars = 40550,
                    forks = 4998
                )
            )
        }
    }
}
