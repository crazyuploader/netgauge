package com.devjugal.netgauge.provider

/** Every speed test site the app knows. Add a new site here. */
object Providers {
    val all: List<SpeedTestProvider> = listOf(FastCom)

    val default: SpeedTestProvider = FastCom

    /** The provider with this [id], or [default] when it is unknown (for example, removed in an update). */
    fun byId(id: String?): SpeedTestProvider = all.firstOrNull { it.id == id } ?: default
}
