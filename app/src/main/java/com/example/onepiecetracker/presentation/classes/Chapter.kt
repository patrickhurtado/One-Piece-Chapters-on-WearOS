package com.example.onepiecetracker.presentation.classes

data class Chapter(val number: String, val name: String?, val link: String) {
    override fun toString(): String {
        if(name.isNullOrEmpty()) {
            return "Ch.$number"
        } else {
            return "Ch.$number: $name"
        }
    }
}

