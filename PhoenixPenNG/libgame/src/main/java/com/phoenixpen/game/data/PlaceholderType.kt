package com.phoenixpen.game.data

/**
 * All types of placeholders used in tree shapes. They represent certain parts of a tree that can
 * be set to actual tree part types by the tree type.
 */
enum class PlaceholderType(val character: Char, val identifier: String)
{
    Empty(' ', "empty"),

    PrimaryLeaf('¼', "primary_leaf"),
    SecondaryLeaf(';', "secondary_leaf"),
    Trunk('O', "trunk"),
    TrunkCap('▴', "trunk_cap"),

    BranchHorz('─', "branch_horz"),
    BranchVert('│', "branch_vert"),
    BranchCross('┼', "branch_cross"),
    BranchSE('┌', "branch_se"),
    BranchNW('┘', "branch_nw"),
    BranchSW('┐', "branch_sw"),
    BranchNE('└', "branch_ne"),
    BranchVertW('┤', "branch_vert_w"),
    BranchVertE('├', "branch_vert_e"),
    BranchHorzS('┬', "branch_horz_s"),
    BranchHorzN('┴', "branch_horz_n"),

    TrunkHorz('═', "trunk_horz"),
    TrunkVert('║', "trunk_vert"),
    TrunkSE('╔', "trunk_se"),
    TrunkNW('╝', "trunk_nw"),
    TrunkNE('╚', "trunk_ne"),
    TrunkSW('╗', "trunk_sw"),
    TrunkCross('╬', "trunk_cross"),
    TrunkVertW('╣', "trunk_vert_w"),
    TrunkVertE('╠', "trunk_vert_e"),
    TrunkHorzS('╦', "trunk_horz_s"),
    TrunkHorzN('╩', "trunk_horz_n");


    /**
     * Reverse look up implementation for this enumeration type
     */
    companion object
    {
        /**
         * Data structure mapping placeholder characters to corresponding enumeration value
         */
        private val map = values().associateBy(PlaceholderType::character)

        /**
         * Retrieve enumeration value from given placeholder character
         *
         * @param character Placeholder character to use for look up
         * @return Enumeration value matching given placeholder character
         */
        fun fromCharacter(character: Char) = map[character] ?: throw IllegalArgumentException("Unknown placeholder character")
    }
}