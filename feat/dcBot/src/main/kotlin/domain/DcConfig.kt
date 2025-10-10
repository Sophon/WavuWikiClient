package domain

import kotlinx.serialization.Serializable

@Serializable
data class DcConfig(
    val heihachiRebornApiKey: String
)
