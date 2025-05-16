package com.example.moviematch.util

import android.content.Context

fun importMoviesFromCsv(context: Context): Map<String, List<String>> {
    val inputStream = context.assets.open("movies.csv")
    val genreMap = mutableMapOf<String, MutableList<String>>()

    inputStream.bufferedReader().useLines { lines ->
        lines.drop(1).forEach { line ->
            val parts = line.split(",")
            if (parts.size < 3) return@forEach

            val title = parts[1].trim()
            val genresRaw = parts[2].trim()

            genresRaw.split(",").forEach { genre ->
                val cleanedGenre = genre.trim()
                genreMap.getOrPut(cleanedGenre) { mutableListOf() }.add(title)
            }
        }
    }

    return genreMap.mapValues { it.value.distinct().sorted() }
}
