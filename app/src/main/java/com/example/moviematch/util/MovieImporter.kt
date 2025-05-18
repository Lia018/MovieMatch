package com.example.moviematch.util

import android.content.Context

/**
 * Imports movie data from a CSV file bundled in the app's assets and returns it as a map.
 *
 * The CSV file should be named "movies.csv" and must be located in the `assets` directory.
 * Each line is expected to be in the format: `title,genres` where genres is a comma-separated list.
 *
 * This function processes the file line by line, splits movie titles by genre, and builds a map
 * of genres to their associated list of movie titles.
 *
 * Duplicate titles per genre are removed and the lists are sorted alphabetically.
 *
 * @param context The Android [Context] used to access the app's assets.
 * @return A [Map] where each key is a genre and the corresponding value is a sorted list
 *         of unique movie titles belonging to that genre.
 */
fun importMoviesFromCsv(context: Context): Map<String, List<String>> {
    // Open the CSV file from the assets directory
    //https://gist.github.com/tiangechen/b68782efa49a16edaf07dc2cdaa855ea#file-movies-csv
    val inputStream = context.assets.open("movies.csv")

    // A mutable map to group movies by genre
    val genreMap = mutableMapOf<String, MutableList<String>>()

    // Read each line from the file and process movie data
    inputStream.bufferedReader().useLines { lines ->
        // Skip the header line and iterate over the rest
        lines.drop(1).forEach { line ->
            val parts = line.split(",")
            // Skip malformed lines
            if (parts.size < 3) return@forEach

            // Extract movie title
            val title = parts[1].trim()
            // Extract genre string
            val genresRaw = parts[2].trim()

            // Split genres and add the title under each associated genre
            genresRaw.split(",").forEach { genre ->
                val cleanedGenre = genre.trim()
                genreMap.getOrPut(cleanedGenre) { mutableListOf() }.add(title)
            }
        }
    }

    // Remove duplicates and sort titles alphabetically per genre
    return genreMap.mapValues { it.value.distinct().sorted() }
}
