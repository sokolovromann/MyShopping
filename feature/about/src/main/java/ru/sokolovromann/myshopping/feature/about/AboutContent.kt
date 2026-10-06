package ru.sokolovromann.myshopping.feature.about

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import ru.sokolovromann.myshopping.core.ui.component.GridItem
import ru.sokolovromann.myshopping.core.ui.component.SimpleVerticalGrid
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Composable
fun AboutContent(apiName: UiText) {
    SimpleVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = 1
    ) {
        item {
            GridItem(
                title = { Text(stringResource(R.string.about_text_developer)) },
                body = { Text(stringResource(R.string.about_data_developer)) }
            )
        }
        item {
            GridItem(
                title = { Text(stringResource(R.string.about_text_app_version)) },
                body = { Text(apiName.asCompose()) }
            )
        }
        item {
            GridItem(
                title = { Text(stringResource(R.string.about_text_links)) },
                body = {
                    LinkText(
                        text = stringResource(R.string.about_text_email),
                        url = createEmailUrl()
                    )
                    LinkText(
                        text = stringResource(R.string.about_text_github),
                        url = stringResource(R.string.about_data_github)
                    )
                    LinkText(
                        text = stringResource(R.string.about_text_privacy_policy),
                        url = stringResource(R.string.about_data_privacy_policy)
                    )
                    LinkText(
                        text = stringResource(R.string.about_text_terms_and_conditions),
                        url = stringResource(R.string.about_data_terms_and_conditions)
                    )
                }
            )
        }
    }
}

@Composable
private fun LinkText(text: String, url: String) {
    val annotatedString = buildAnnotatedString {
        withLink(
            link = LinkAnnotation.Url(
                url = url,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            ),
            block = { append(text) }
        )
    }
    Text(annotatedString)
}

@Composable
private fun createEmailUrl(): String {
    val url = stringResource(R.string.about_data_email)
    val subject = stringResource(R.string.about_text_feedback)
    val encodedSubject = Uri.encode(subject)
    return "$url?subject=$encodedSubject"
}