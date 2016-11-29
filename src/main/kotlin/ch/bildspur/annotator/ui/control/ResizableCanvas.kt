package ch.bildspur.annotator.ui.control

import javafx.scene.canvas.Canvas

/**
 * Created by cansik on 29.11.16.
 */
class ResizableCanvas : Canvas() {
    override fun isResizable() = true
}