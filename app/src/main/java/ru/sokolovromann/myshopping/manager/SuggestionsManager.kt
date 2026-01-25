package ru.sokolovromann.myshopping.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.sokolovromann.myshopping.data39.suggestions.AddSuggestionWithDetails
import ru.sokolovromann.myshopping.data39.suggestions.SortSuggestions
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetail
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailsRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionWithDetails
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfig
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfigRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsPreInstalled
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsViewMode
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetailsInfo
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestions
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOnIo
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime
import javax.inject.Inject

class SuggestionsManager @Inject constructor(
    private val suggestionsRepository: SuggestionsRepository,
    private val suggestionsConfigRepository: SuggestionsConfigRepository,
    private val detailsRepository: SuggestionDetailsRepository
) {

    fun observeSuggestionsWithDetails(): Flow<Collection<SuggestionWithDetails>> {
        return combine(
            flow = suggestionsRepository.observeAll(),
            flow2 = observeConfig(),
            transform = { suggestions, config ->
                suggestions
                    .sorted(config.sort)
                    .takeDetails(config.takeDetails)
            }
        ).flowOnIo()
    }

    fun observeSuggestionWithDetails(uid: UID): Flow<SuggestionWithDetails?> {
        return combine(
            flow = suggestionsRepository.observe(uid),
            flow2 = observeConfig(),
            transform = { foundSuggestion, config ->
                foundSuggestion?.copy(
                    suggestion = foundSuggestion.suggestion,
                    details = foundSuggestion.details.take(config.takeDetails)
                )
            }
        ).flowOnIo()
    }

    fun observeConfig(): Flow<SuggestionsConfig> {
        return suggestionsConfigRepository.observe().flowOnIo()
    }

    suspend fun getSuggestionsWithDetails(): Collection<SuggestionWithDetails> = withIoContext {
        val suggestions = suggestionsRepository.getAll()
        val config = getConfig()
        return@withIoContext suggestions
            .sorted(config.sort)
            .takeDetails(config.takeDetails)
    }

    suspend fun getSuggestionWithDetails(uid: UID): SuggestionWithDetails? = withIoContext {
        val suggestion = suggestionsRepository.get(uid)
        val config = getConfig()
        return@withIoContext suggestion?.copy(
            suggestion = suggestion.suggestion,
            details = suggestion.details.take(config.takeDetails)
        )
    }

    suspend fun getSuggestions(): Collection<Suggestion> = withIoContext {
        return@withIoContext getSuggestionsWithDetails().map { it.suggestion }
    }

    suspend fun getDetails(): SuggestionDetails = withIoContext {
        return@withIoContext detailsRepository.getAll()
    }

    suspend fun getConfig(): SuggestionsConfig = withIoContext {
        return@withIoContext suggestionsConfigRepository.get()
    }

    suspend fun addSuggestions(suggestions: Collection<Suggestion>): Unit = withIoContext {
        suggestionsRepository.insertAll(suggestions)
    }

    suspend fun addSuggestion(suggestion: Suggestion): Unit = withIoContext {
        suggestionsRepository.insert(suggestion)
    }

    suspend fun addDetails(details: Collection<SuggestionDetail>): Unit = withIoContext {
        detailsRepository.insertAll(details)
    }

    suspend fun addDetail(detail: SuggestionDetail): Unit = withIoContext {
        detailsRepository.insert(detail)
    }

    suspend fun addConfig(config: SuggestionsConfig): Unit = withIoContext {
        suggestionsConfigRepository.update(config)
    }

    suspend fun updateConfig(preInstalled: SuggestionsPreInstalled): Unit = withIoContext {
        val config = getConfig().copy(preInstalled = preInstalled)
        addConfig(config)
    }

    suspend fun updateConfig(viewMode: SuggestionsViewMode): Unit = withIoContext {
        val config = getConfig().copy(viewMode = viewMode)
        addConfig(config)
    }

    suspend fun updateConfig(sort: SortSuggestions): Unit = withIoContext {
        val config = getConfig().copy(sort = sort)
        addConfig(config)
    }

    suspend fun updateConfig(add: AddSuggestionWithDetails): Unit = withIoContext {
        val config = getConfig().copy(add = add)
        addConfig(config)
    }

    suspend fun updateConfig(take: TakeSuggestions): Unit = withIoContext {
        val config = getConfig().copy(takeSuggestions = take)
        addConfig(config)
    }

    suspend fun updateConfig(take: TakeSuggestionDetailsInfo): Unit = withIoContext {
        val config = getConfig().copy(takeDetails = take)
        addConfig(config)
    }

    suspend fun deleteSuggestionsWithDetails(uids: Collection<UID>): Unit = withIoContext {
        suggestionsRepository.deleteAll(uids)
        uids.forEach { deleteDetails(it) }
    }

    suspend fun deleteDetails(directory: UID): Unit = withIoContext {
        val details = getDetails()
        fun toUid(list: Collection<SuggestionDetail>) = list
            .filter { it.getDirectory() == directory }
            .map { it.getUid() }
        mutableListOf<UID>().apply {
            addAll(toUid(details.images))
            addAll(toUid(details.manufacturers))
            addAll(toUid(details.brands))
            addAll(toUid(details.sizes))
            addAll(toUid(details.colors))
            addAll(toUid(details.quantities))
            addAll(toUid(details.unitPrices))
            addAll(toUid(details.discounts))
            addAll(toUid(details.taxRates))
            addAll(toUid(details.costs))
        }.let { deleteDetails(directory, it) }
    }

    suspend fun deleteDetails(directory: UID, uids: Collection<UID>): Unit = withIoContext {
        detailsRepository.deleteAll(uids)
        suggestionsRepository.get(directory)?.suggestion
            ?.copy(lastModified = DateTime.getCurrent())
            ?.let { suggestionsRepository.insert(it) }
    }

    suspend fun clearSuggestions(): Unit = withIoContext {
        suggestionsRepository.clear()
    }

    suspend fun clearDetails(): Unit = withIoContext {
        detailsRepository.clear()
    }

    private fun Collection<SuggestionWithDetails>.sorted(
        sort: SortSuggestions
    ): Collection<SuggestionWithDetails> {
        return when (sort) {
            is SortSuggestions.Name -> {
                when (sort.order) {
                    SortSuggestions.Order.ByAscending -> sortedBy { it.suggestion.name }
                    SortSuggestions.Order.ByDescending -> sortedByDescending { it.suggestion.name }
                }
            }
            is SortSuggestions.Popularity -> {
                when (sort.order) {
                    SortSuggestions.Order.ByAscending -> sortedBy { it.suggestion.used }
                    SortSuggestions.Order.ByDescending -> sortedByDescending { it.suggestion.used }
                }
            }
        }
    }

    private fun Collection<SuggestionWithDetails>.takeDetails(
        takeDetails: TakeSuggestionDetailsInfo
    ): Collection<SuggestionWithDetails> {
        return map {
            it.copy(details = it.details.take(takeDetails))
        }
    }

    private fun SuggestionDetails.take(
        takeDetails: TakeSuggestionDetailsInfo
    ): SuggestionDetails {
        fun <T> Iterable<T>.take(take: TakeSuggestionDetails): List<T> = when (take) {
            TakeSuggestionDetails.All -> this
            TakeSuggestionDetails.One -> this.take(1)
            TakeSuggestionDetails.Three -> this.take(3)
            TakeSuggestionDetails.Five -> this.take(5)
            TakeSuggestionDetails.Ten -> this.take(10)
            TakeSuggestionDetails.DoNotTake -> emptyList()
        }.toList()
        return SuggestionDetails(
            images = images
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.descriptions),
            manufacturers = manufacturers
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.descriptions),
            brands = brands
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.descriptions),
            sizes = sizes
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.descriptions),
            colors = colors
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.descriptions),
            quantities = quantities
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.quantities),
            unitPrices = unitPrices
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.money),
            discounts = discounts
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.money),
            taxRates = taxRates
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.money),
            costs = costs
                .sortedByDescending { it.value.created.getMillis() }
                .take(takeDetails.money)
        )
    }
}