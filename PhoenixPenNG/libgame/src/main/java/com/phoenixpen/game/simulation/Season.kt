package com.phoenixpen.game.simulation

/**
 * An enumeration describing the four seasons in a year
 *
 * @property index the index of the season type in this enumeration
 */
enum class Season(val index: Int)
{
    Spring(0),
    Summer(1),
    Autumn(2),
    Winter(3);

    /**
     * Given a season, determine the next season.
     */
    fun nextSeason(): Season
    {
        // Calculate new index
        val newIndex = (this.index + 1) % 4

        // Return new season
        return seasonFromIndex(newIndex)
    }

    companion object
    {
        /**
         * Retrieve season for given index
         *
         * @param index Index to retrieve matching season for
         * @return Season matching given index
         */
        fun seasonFromIndex(index: Int): Season
        {
            return when(index)
            {
                0 -> Spring
                1 -> Summer
                2 -> Autumn
                3 -> Winter
                else -> throw IllegalStateException("Unknown season index: $index")
            }
        }
    }
}