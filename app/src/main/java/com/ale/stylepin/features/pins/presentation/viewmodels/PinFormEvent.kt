package com.ale.stylepin.features.pins.presentation.viewmodels

sealed class PinFormEvent {
    data class TitleChanged(val value: String) : PinFormEvent()
    data class DescriptionChanged(val value: String) : PinFormEvent()
    data class ImageUrlChanged(val value: String) : PinFormEvent()
    data class CategoryChanged(val value: String) : PinFormEvent()
    data class SeasonChanged(val value: String) : PinFormEvent()
    data class PriceRangeChanged(val value: String) : PinFormEvent()
    data class WhereToBuyChanged(val value: String) : PinFormEvent()
    data class PurchaseLinkChanged(val value: String) : PinFormEvent()
    data class IsPrivateChanged(val value: Boolean) : PinFormEvent()
    data class StylesChanged(val value: List<String>) : PinFormEvent()
    data class OccasionsChanged(val value: List<String>) : PinFormEvent()
    data class BrandsChanged(val value: List<String>) : PinFormEvent()
    data class ColorsChanged(val value: List<String>) : PinFormEvent()
    data class TagsChanged(val value: List<String>) : PinFormEvent()
}