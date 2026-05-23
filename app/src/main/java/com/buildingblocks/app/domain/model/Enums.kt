package com.buildingblocks.app.domain.model

enum class SetStatus {
    SEALED,
    UNBUILT,
    IN_PROGRESS,
    BUILT,
    DISASSEMBLED,
    MISSING_PARTS,
    WAITING_FOR_DISPLAY_SPACE,
    SOLD;

    fun displayName(): String = when (this) {
        SEALED -> "Sealed"
        UNBUILT -> "Unbuilt"
        IN_PROGRESS -> "In Progress"
        BUILT -> "Built"
        DISASSEMBLED -> "Disassembled"
        MISSING_PARTS -> "Missing Parts"
        WAITING_FOR_DISPLAY_SPACE -> "Waiting for Display"
        SOLD -> "Sold"
    }

    fun isBacklog(): Boolean = this in listOf(SEALED, UNBUILT, IN_PROGRESS, MISSING_PARTS, WAITING_FOR_DISPLAY_SPACE)
}

enum class Difficulty {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT;

    fun displayName(): String = when (this) {
        BEGINNER -> "Beginner"
        INTERMEDIATE -> "Intermediate"
        ADVANCED -> "Advanced"
        EXPERT -> "Expert"
    }
}

enum class Priority {
    LOW, MEDIUM, HIGH;

    fun displayName(): String = when (this) {
        LOW -> "Low"
        MEDIUM -> "Medium"
        HIGH -> "High"
    }
}

enum class AuthProvider { GOOGLE, EMAIL }

enum class PremiumStatus { FREE, PREMIUM }

enum class StorageLocationType {
    CABINET, SHELF, BOX, DRAWER, COMPARTMENT, OTHER;

    fun displayName(): String = when (this) {
        CABINET -> "Cabinet"
        SHELF -> "Shelf"
        BOX -> "Box"
        DRAWER -> "Drawer"
        COMPARTMENT -> "Compartment"
        OTHER -> "Other"
    }
}

enum class MissingPartStatus {
    NEEDED, ORDERED, RECEIVED;

    fun displayName(): String = when (this) {
        NEEDED -> "Needed"
        ORDERED -> "Ordered"
        RECEIVED -> "Received"
    }
}

enum class SortOrder {
    DATE_ADDED_DESC,
    DATE_ADDED_ASC,
    NAME_ASC,
    NAME_DESC,
    PIECE_COUNT_DESC,
    PIECE_COUNT_ASC,
    PRIORITY_HIGH,
    DIFFICULTY_HIGH;

    fun displayName(): String = when (this) {
        DATE_ADDED_DESC -> "Newest first"
        DATE_ADDED_ASC -> "Oldest first"
        NAME_ASC -> "Name A–Z"
        NAME_DESC -> "Name Z–A"
        PIECE_COUNT_DESC -> "Most pieces"
        PIECE_COUNT_ASC -> "Fewest pieces"
        PRIORITY_HIGH -> "Highest priority"
        DIFFICULTY_HIGH -> "Hardest first"
    }
}
