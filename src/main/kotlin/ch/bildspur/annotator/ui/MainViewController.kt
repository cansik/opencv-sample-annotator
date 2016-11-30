package ch.bildspur.annotator.ui

import ch.bildspur.annotator.geometry.Vector2
import ch.bildspur.annotator.model.AnnotationImage
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Alert
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import kotlin.properties.Delegates

/**
 * Created by cansik on 29.11.16.
 */
class MainViewController {
    @FXML
    var canvas: Canvas? = null

    var positivesFile: File by Delegates.notNull()

    var annotationImages: List<AnnotationImage> by Delegates.notNull()

    var activeImage: AnnotationImage by Delegates.notNull()

    var imageIterator: Iterator<AnnotationImage> by Delegates.notNull()

    var stage: Stage by Delegates.notNull()

    var gc: GraphicsContext by Delegates.notNull()

    var scaleFactor = 1.0

    fun handleWindowShownEvent() {
        showSettingsView()
    }

    fun showSettingsView() {
        val loader = FXMLLoader()
        val root = loader.load<Parent>(javaClass.classLoader.getResourceAsStream("view/SettingsView.fxml"))
        val controller = loader.getController<SettingsViewController>()

        val stage = Stage()
        stage.initModality(Modality.WINDOW_MODAL)
        stage.title = "OpenCV Sample Annotator Settings"
        stage.scene = Scene(root)
        stage.showAndWait()

        if (!controller.isStart)
            stage.close()

        positivesFile = controller.positivesFile!!
        annotationImages = controller.datasetFiles!!.map(::AnnotationImage)
        imageIterator = annotationImages.iterator()

        // setup canvas
        canvas!!.onKeyPressed = EventHandler<KeyEvent> { handleKeyEvent(it) }
        canvas!!.onMouseDragged = EventHandler<MouseEvent> { handleMouseDragged(it) }
        canvas!!.onMouseClicked = EventHandler<MouseEvent> { handleMouseClicked(it) }

        canvas!!.isFocusTraversable = true

        gc = canvas!!.graphicsContext2D
        resizeCanvas()

        loadNextImage()
    }

    fun loadNextImage() {
        if (!imageIterator.hasNext()) {
            saveAndClose()
            return
        }

        activeImage = imageIterator.next()
        initImage()
    }

    fun saveAndClose() {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "OpenCV Sample Annotator"
        alert.headerText = "All images annotated!"
        alert.contentText = "The data has been saved to: ${positivesFile.name}"

        alert.showAndWait()

        stage.close()
    }

    fun resizeCanvas() {
        canvas!!.width = stage.width
        canvas!!.height = stage.height - 100.0
    }

    fun initImage() {
        calculateScaleFactor()
        drawInfo()
    }

    fun calculateScaleFactor() {
        // calculate scale size of image
        val portrait = activeImage.image.width < activeImage.image.height

        val screenSize = Vector2(canvas!!.width, canvas!!.height)
        val imageSize = Vector2(activeImage.image.width, activeImage.image.height)

        if (portrait)
            scaleFactor = screenSize.x / imageSize.x
        else
            scaleFactor = screenSize.y / imageSize.y
    }

    fun drawInfo() {
        gc.clearRect(0.0, 0.0, canvas!!.width, canvas!!.height)
        gc.drawImage(activeImage.image, 0.0, 0.0,
                activeImage.image.width * scaleFactor,
                activeImage.image.height * scaleFactor)

        gc.stroke = Color.GREEN

        // draw polygons
        for (polygon in activeImage.polygons) {
            gc.strokePolygon(
                    polygon.points.map { vectorToScreen(it).x }.toDoubleArray(),
                    polygon.points.map { vectorToScreen(it).y }.toDoubleArray(),
                    polygon.points.size)
        }
    }

    fun handleKeyEvent(e: KeyEvent) {
        when (e.code) {
            KeyCode.SPACE -> nextClicked(ActionEvent())
            KeyCode.C -> clearClicked(ActionEvent())
        }
    }

    fun handleMouseDragged(e: MouseEvent) {
        println("Dragged: ${e.x} | ${e.y}")
    }

    fun handleMouseClicked(e: MouseEvent) {
        println("Clicked: ${e.x} | ${e.y}")
    }

    fun clearClicked(e: ActionEvent) {
        activeImage.polygons.clear()
    }

    fun nextClicked(e: ActionEvent) {
        loadNextImage()
    }

    fun vectorToScreen(v: Vector2): Vector2 {
        return v.scale(scaleFactor)
    }

    fun vectorToImage(v: Vector2): Vector2 {
        return v.scale(1.0 / scaleFactor)
    }

}