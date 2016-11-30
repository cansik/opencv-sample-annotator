package ch.bildspur.annotator.geometry

import java.io.Serializable

/**
 * Created by cansik on 28.11.16.
 */
class Polygon2() : Serializable {

    val points = mutableListOf<Vector2>()

    constructor(vararg points: Vector2) : this() {
        this.points.addAll(points)
    }

    operator fun get(i: Int): Vector2 {
        return points[i]
    }

    operator fun set(i: Int, v: Vector2) {
        points[i] = v
    }

    /**
     * PNPoly Method:
     * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html#The%20C%20Code
     */
    fun contains(p: Vector2): Boolean {
        var inside = false
        var i = 0
        var j = points.size - 1
        while (i < points.size) {
            if (points[i].y > p.y != points[j].y > p.y
                    && p.x < (points[j].x - points[i].x)
                    * (p.y - points[i].y)
                    / (points[j].y - points[i].y) + points[i].x)
                inside = !inside
            j = i++
        }
        return inside
    }

    /**
     * Finds the best position to insert the point.
     */
    fun addAndAdaptPoint(p: Vector2) {
        var min = Double.MAX_VALUE
        var index = -1

        for (i in 0..points.size - 1) {
            val p1 = points[i]
            val p2 = points[(i + 1) % points.size]

            val v = Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
            val distance = v.distance(p)

            if (distance < min) {
                min = distance
                index = i + 1
            }
        }

        points.add(index, p)
    }

    companion object {

        private const val serialVersionUID = 1L

    }

}