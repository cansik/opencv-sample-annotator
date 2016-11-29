package ch.bildspur.annotator.ui

import ch.bildspur.annotator.model.AnnotationImage
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
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

        gc = canvas!!.graphicsContext2D
        resizeCanvas()

        loadNextImage()
    }

    fun loadNextImage() {
        if (!imageIterator.hasNext())
            saveAndClose()

        activeImage = imageIterator.next()
        initImage()

        println("image loaded!")
    }

    fun saveAndClose() {
        stage.close()
    }

    fun resizeCanvas() {
        canvas!!.width = stage.width
        canvas!!.height = stage.height
    }

    fun initImage() {
        gc.clearRect(0.0, 0.0, canvas!!.width, canvas!!.height)
        gc.drawImage(activeImage.image, 0.0, 0.0)

        drawInfo()
    }

    fun drawInfo() {
        
    }

    fun handleKeyEvent(e: KeyEvent) {
        when (e.code) {
            KeyCode.SPACE -> loadNextImage()
            KeyCode.C -> activeImage.polygons.clear()
        }
    }

    fun handleMouseDragged(e: MouseEvent) {

    }

    fun handleMouseClicked(e: MouseEvent) {

    }
}