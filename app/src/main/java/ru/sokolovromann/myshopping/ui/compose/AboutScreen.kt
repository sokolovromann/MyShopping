package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.AboutScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.AboutViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AboutEvent
import androidx.core.net.toUri

@Composable
fun AboutScreen(
    navController: NavController,
    viewModel: AboutViewModel = hiltViewModel()
) {
    val state = viewModel.aboutState
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val developerEmail = stringResource(R.string.data_email_developer)
    val subjectText = stringResource(R.string.data_email_subject)
    val gitHubLink = stringResource(R.string.data_link_github)
    val privacyPolicyLink = stringResource(R.string.data_link_privacy_policy)
    val termsAndConditionsLink = stringResource(R.string.data_link_terms_and_conditions)

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect { event ->
            when (event) {
                AboutScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }

                AboutScreenEvent.OnSendEmailToDeveloper -> navController.chooseNavigate(
                    intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:".toUri()
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(developerEmail))
                        putExtra(Intent.EXTRA_SUBJECT, subjectText)
                    }
                )

                AboutScreenEvent.OnShowAppGithub -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        gitHubLink.toUri()
                    )
                )

                AboutScreenEvent.OnShowPrivacyPolicy -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        privacyPolicyLink.toUri()
                    )
                )

                AboutScreenEvent.OnShowTermsAndConditions -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        termsAndConditionsLink.toUri()
                    )
                )

                is AboutScreenEvent.OnDrawerScreenSelected -> {
                    navController.navigateWithDrawerOption(route = event.drawerScreen.getScreen())
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                is AboutScreenEvent.OnSelectDrawerScreen -> {
                    if (event.display) {
                        scaffoldState.drawerState.open()
                    } else {
                        scaffoldState.drawerState.close()
                    }
                }
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(AboutEvent.OnSelectDrawerScreen(false))
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppTopAppBar(
                title = { Text(text = stringResource(R.string.about_header_aboutApp)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val event = AboutEvent.OnSelectDrawerScreen(display = true)
                            viewModel.onEvent(event)
                        }
                    ) {
                        DefaultIcon(
                            icon = UiIcon.NavigationMenu,
                            contentDescription = UiString.FromResources(R.string.all_contentDescription_navigationMenuIcon)
                        )
                    }
                }
            )
        },
        drawerContent = {
            AppDrawerContent(
                selected = DrawerScreen.ABOUT.toUiRoute(),
                onItemClick = {
                    val event = AboutEvent.OnDrawerScreenSelected(it.toDrawerScreen())
                    viewModel.onEvent(event)
                }
            )
        }
    ) { paddings ->
        AboutContent(scaffoldPaddings = paddings) {
            AppSurfaceItem(
                onClick = {},
                clickableEnabled = false,
                title = { Text(text = stringResource(R.string.about_text_developer)) },
                body = {
                    Text(text = stringResource(R.string.data_text_developerName))
                    AboutTextButton(text = stringResource(R.string.data_text_developerEmail)) {
                        viewModel.onEvent(AboutEvent.OnClickEmail)
                    }
                }
            )
            AppSurfaceItem(
                onClick = {},
                clickableEnabled = false,
                title = { Text(text = stringResource(R.string.about_text_appVersion)) },
                body = { Text(text = state.appVersion) }
            )
            AppSurfaceItem(
                onClick = {},
                clickableEnabled = false,
                title = { Text(text = stringResource(R.string.about_text_links)) },
                body = {
                    AboutTextButton(text = stringResource(R.string.about_text_github)) {
                        viewModel.onEvent(AboutEvent.OnClickGitHub)
                    }
                    AboutTextButton(text = stringResource(R.string.about_text_privacy_policy)) {
                        viewModel.onEvent(AboutEvent.OnClickPrivacyPolicy)
                    }
                    AboutTextButton(text = stringResource(R.string.about_text_terms_and_conditions)) {
                        viewModel.onEvent(AboutEvent.OnClickTermsAndConditions)
                    }
                }
            )
        }
    }
}

@Composable
private fun AboutContent(
    scaffoldPaddings: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(scaffoldPaddings)
            .padding(AboutContentPaddings)
    ) {
        content()
        Spacer(modifier = Modifier.height(AboutSpacerHeight))
    }
}

@Composable
private fun AboutTextButton(
    text: String,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .clickable { onClick() }
            .padding(AboutButtonPaddings),
        text = text,
        color = MaterialTheme.colors.primary,
        textDecoration = TextDecoration.Underline
    )
}

private val AboutContentPaddings = PaddingValues(
    horizontal = 8.dp,
    vertical = 4.dp
)
private val AboutSpacerHeight = 128.dp
private val AboutButtonPaddings = PaddingValues(vertical = 2.dp)