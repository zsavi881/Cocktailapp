package fr.isen.savi.thegreatestcocktailapp.data

import android.content.Context

class FavoritesStore(context: Context) {
    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun getAll(): Set<String> = prefs.getStringSet("ids", emptySet()) ?: emptySet()

    fun isFavorite(id: String): Boolean = getAll().contains(id)

    fun toggle(id: String): Set<String> {
        val current = getAll().toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        prefs.edit().putStringSet("ids", current).apply()
        return current
    }
}