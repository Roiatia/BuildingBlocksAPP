package com.buildingblocks.app.domain.usecase

object ValidateAddSetUseCase {
    data class Result(val isValid: Boolean, val errorMessage: String?)

    operator fun invoke(name: String, legoSetNumber: String): Result {
        return if (name.isBlank() && legoSetNumber.isBlank()) {
            Result(isValid = false, errorMessage = "Enter a set name or set number")
        } else {
            Result(isValid = true, errorMessage = null)
        }
    }
}
