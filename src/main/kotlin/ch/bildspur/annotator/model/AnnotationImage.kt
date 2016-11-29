package ch.bildspur.annotator.model

import ch.bildspur.annotator.geometry.Polygon2
import javafx.scene.image.Image
import java.io.File

/**
 * Created by cansik on 29.11.16.
 */
class AnnotationImage(val file: File) {

    val name = file.nameWithoutExtension

    val polygons = mutableListOf<Polygon2>()

    val image: Image by lazy {
        Image(file.toURI().toString())
    }
}