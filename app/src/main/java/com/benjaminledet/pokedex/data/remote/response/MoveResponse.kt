package com.benjaminledet.pokedex.data.remote.response

import com.google.gson.annotations.SerializedName

data class MoveResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: ApiResourceResponse,

    @SerializedName("accuracy")
    val accuracy: Int,

    @SerializedName("power")
    val power: Int,

    @SerializedName("pp")
    val pp: Int
)