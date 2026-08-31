package com.example.data.model

enum class SyncStatus {
    LOCAL_ONLY,
    WAITING_SYNC,
    SYNCED
}

enum class AcademicLevel(val displayName: String) {
    SCHOOL("School"),
    SSC("SSC"),
    HSC("HSC"),
    DIPLOMA("Diploma"),
    UNIVERSITY("University"),
    OTHER("Other")
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

enum class DocumentType {
    PDF,
    IMAGE,
    NOTE_SCAN,
    DOCUMENT
}
