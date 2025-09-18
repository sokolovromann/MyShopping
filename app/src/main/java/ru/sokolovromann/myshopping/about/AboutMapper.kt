package ru.sokolovromann.myshopping.about

import ru.sokolovromann.myshopping.io.Mapper
import javax.inject.Inject

class AboutMapper @Inject constructor() : Mapper<AboutEntity, About>() {

    override fun mapTo(a: AboutEntity): About {
        return About(
            id = a.id,
            api = a.api,
            version = a.version,
            developer = a.developer.orEmpty(),
            email = a.email.orEmpty(),
            linkedin = a.linkedin.orEmpty(),
            github = a.github.orEmpty(),
            privacyPolicy = a.privacyPolicy.orEmpty(),
            termsOfConditions = a.termsOfConditions.orEmpty()
        )
    }
}