package com.phoenixpen.game.math

import com.phoenixpen.game.ascii.Dimensions
import com.phoenixpen.game.ascii.Position
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList
import kotlin.math.*

/**
 * A type alias for positions in the grid used by [PoissonDiskSampler]
 */
private typealias GridPoint = Position

/**
 * A random point sampler based on the poisson disk sampling algorithm.
 *
 * @param dimensions Dimensions of the area to sample from, in map cells
 * @property minDistance Minimum distance between sampling points.
 * @property maxTries Max number of points tried in each generation before algorithm gives up
 * @property minDistanceMod Normal distribution of minimum distance modifier value
 */
class PoissonDiskSampler(
        dimensions: Dimensions,
        val minDistance: Double,
        val maxTries: Int = 30,
        val minDistanceMod: NormalDistribution = NormalDistribution(probability = 0.0)
): SamplingStrategy(dimensions)
{
    /**
     * A class describing a floating point, two-dimensional position
     */
    private data class Position2d(var x: Double, var y: Double)
    {
        fun toPosition(): Position
        {
            return Position(x.toInt(), y.toInt())
        }

        fun distanceTo(other: Position2d): Double
        {
            val diff = Position2d(this.x - other.x, this.y - other.y)

            return sqrt(diff.x * diff.x + diff.y * diff.y)
        }
    }

    /**
     * An internal class used to implement a two-dimensional grid, with entries potentially
     * containing point instances
     *
     * @property dimensions Dimensions of this grid
     */
    private class Grid(val dimensions: Dimensions)
    {
        /**
         * All entries of this grid
         */
        private val entries = ArrayList<Optional<Position2d>>(dimensions.height * dimensions.width)

        /**
         * Initialize empty grid
         */
        init
        {
            for(ix in 0 until this.dimensions.width)
            {
                for(iy in 0 until this.dimensions.height)
                {
                    this.entries.add(Optional.empty())
                }
            }
        }

        /**
         * Set point in grid
         */
        fun setPoint(pos: GridPoint, point: Position2d)
        {
            this.entries[this.getIndex(pos)] = Optional.of(point)
        }

        /**
         * Check if there is a stored point at given grid cell.
         *
         * @param pos Grid cell position to check
         * @return Flag indicating whether given grid cell contains a point.
         */
        fun hasPoint(pos: GridPoint): Boolean
        {
            return this.entries[this.getIndex(pos)].isPresent
        }

        /**
         * Retrieve stored point at given grid cell, if any.
         *
         * @param pos Grid cell position to retrieve point from
         * @return If available, stored point at given grid cell.
         */
        fun getPoint(pos: GridPoint): Position2d
        {
            if(!this.hasPoint(pos))
                throw IllegalStateException("Given cell does not contain a point")

            return this.entries[this.getIndex(pos)].get()
        }

        /**
         * Retrieve internal, linear index for given grid point
         *
         * @param pos Grid position to retrieve linear index for
         * @return Linear index for given grid position
         */
        private fun getIndex(pos: GridPoint): Int
        {
            return (this.dimensions.width * pos.y) + pos.x
        }
    }

    /**
     * Cell size of the internal grid used in the sampling process
     */
    private val cellSize = floor(this.minDistance / Math.sqrt(2.0))

    /**
     * The dimensions of the internal grid
     */
    private val gridDimensions: Dimensions

    /**
     * Initialize values that can be used for multiple sampling runs
     */
    init
    {
        // The internal grid dimensions
        val gridWidth = ceil(this.dimensions.width.toDouble() / this.cellSize).toInt() + 1
        val gridHeight = ceil(this.dimensions.height.toDouble() / this.cellSize).toInt() + 1

        this.gridDimensions = Dimensions(gridWidth, gridHeight)
    }

    override fun sample(): Collection<Position>
    {
        // Create the grid
        val grid = Grid(this.gridDimensions)

        // The list of points to return in the end
        val points = ArrayList<Position2d>()

        // A list of currently "active" points
        val active = RandomQueue<Position2d>()

        // Pick the first, randomly chosen point
        val random = ThreadLocalRandom.current()
        val initial = Position2d(random.nextDouble(0.0, this.dimensions.width.toDouble()), random.nextDouble(0.0, this.dimensions.height.toDouble()))

        // Insert initial point into the grid and all data structures
        this.insertPoint(grid, initial)
        points.add(initial)
        active.push(initial)

        // Work until all active points are used up
        while(active.isNotEmpty())
        {
            // Retrieve next point randomly
            val p = active.pop()

            // Possible calculate modifier for minimum distance
            val mod = when (this.minDistanceMod.shouldGenerate()) {
                true -> this.minDistanceMod.nextValue()
                false -> 0.0
            }

            for(tries in 1 .. this.maxTries)
            {
                // Pick a random angle
                val theta = 2.0 * PI * random.nextDouble(1.0)

                // Pick random radius
                val newRadius = random.nextDouble(this.minDistance, 2.0 * this.minDistance)

                // Calculate coordinates relative to point p
                val pNew = Position2d(p.x + newRadius * cos(theta), p.y + newRadius * sin(theta))

                // Check if the generated point is valid. If not, continue searching
                if(!this.isValidPoint(grid, pNew, (this.minDistance + mod).coerceAtLeast(0.0)))
                    continue

                // The new point is valid, add it to the result and stop searching
                points.add(pNew)
                this.insertPoint(grid, pNew)
                active.push(pNew)
                active.push(p)
                break
            }
        }

        // Round all points
        return points.map { x -> x.toPosition() }
    }

    /**
     * Insert given point into the internal grid
     *
     * @param grid The grid to modify
     * @param point The point to insert
     */
    private fun insertPoint(grid: Grid, point: Position2d)
    {
        val ix = floor(point.x / this.cellSize).toInt()
        val iy = floor(point.y / this.cellSize).toInt()

        grid.setPoint(GridPoint(ix, iy), point)
    }

    private fun isValidPoint(grid: Grid, p: Position2d, minDist: Double): Boolean
    {
        // Perform range check with the screen
        if(p.x < 0.0 || p.x >= this.dimensions.width || p.y < 0.0 || p.y >= this.dimensions.height)
        {
            return false
        }

        val ix = floor(p.x / this.cellSize).toInt()
        val iy = floor(p.y / this.cellSize).toInt()

        val i0 = max(ix - 1, 0)
        val i1 = min(ix + 1, this.gridDimensions.width - 1)
        val j0 = max(iy - 1, 0)
        val j1 = min(iy + 1, this.gridDimensions.height - 1)

        for(i in i0 .. i1)
        {
            for(j in j0 .. j1)
            {
                val gridPoint = GridPoint(i, j)
                if(grid.hasPoint(gridPoint))
                {
                    if(grid.getPoint(gridPoint).distanceTo(p) < minDist)
                        return false
                }
            }
        }

        return true
    }
}