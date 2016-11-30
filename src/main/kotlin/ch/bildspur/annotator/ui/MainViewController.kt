package ch.bildspur.annotator.ui

import ch.bildspur.annotator.geometry.Polygon2
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
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.properties.Delegates
import kotlin.system.exitProcess


/**
 * Created by cansik on 29.11.16.
 */
class MainViewController {
    @FXML
    var canvas: Canvas? = null

    @FXML
    var infoText: Label? = null

    var positivesFile: File by Delegates.notNull()

    var annotationImages: List<AnnotationImage> by Delegates.notNull()

    var activeImage: AnnotationImage by Delegates.notNull()

    var imageIterator: Iterator<AnnotationImage> by Delegates.notNull()

    var stage: Stage by Delegates.notNull()

    var gc: GraphicsContext by Delegates.notNull()

    var scaleFactor = 1.0

    var imageCounter = 0

    fun handleWindowShownEvent() {
        showSettingsView()
    }

    fun showSettingsView() {
        val loader = FXMLLoader()
        val root = loader.load<Parent>(javaClass.classLoader.getResourceAsStream("view/SettingsView.fxml"))
        val controller = loader.getController<SettingsViewController>()

        val settingStage = Stage()
        settingStage.initOwner(stage)
        settingStage.initModality(Modality.WINDOW_MODAL)
        settingStage.title = "OpenCV Sample Annotator Settings"
        settingStage.scene = Scene(root)
        settingStage.showAndWait()

        if (!controller.isStart) {
            settingStage.close()
            exitProcess(0)
        }

        // listeners for stage resize
        stage.widthProperty().addListener({ observableValue, oldSceneWidth, newSceneWidth -> stageResized() })
        stage.heightProperty().addListener({ observableValue, oldSceneHeight, newSceneHeight -> stageResized() })

        positivesFile = controller.positivesFile!!
        annotationImages = controller.datasetFiles!!.map(::AnnotationImage)
        imageIterator = annotationImages.iterator()

        // setup canvas
        canvas!!.onKeyPressed = EventHandler<KeyEvent> { handleKeyEvent(it) }
        canvas!!.onMouseDragged = EventHandler<MouseEvent> { handleMouseDragged(it) }
        canvas!!.onMousePressed = EventHandler<MouseEvent> { handleMousePressed(it) }
        canvas!!.onMouseReleased = EventHandler<MouseEvent> { handleMouseReleased(it) }

        canvas!!.isFocusTraversable = true
        canvas!!.requestFocus()

        gc = canvas!!.graphicsContext2D
        resizeCanvas()

        loadNextImage()
    }

    fun stageResized() {
        resizeCanvas()
        initImage()
    }

    fun loadNextImage() {
        imageCounter++

        if (!imageIterator.hasNext())
            saveAndClose()

        activeImage = imageIterator.next()
        initImage()
    }

    fun saveAndClose() {
        savePolygonsAsText()

        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "OpenCV Sample Annotator"
        alert.headerText = "All images annotated!"
        alert.contentText = "The data has been saved to: ${positivesFile.name}"

        alert.showAndWait()

        stage.close()
        exitProcess(0)
    }

    fun savePolygonsAsText() {
        var sb = StringBuilder()

        for (image in annotationImages) {
            sb.append("${image.file.absolutePath} ")
            for (polygon in image.polygons) {
                val rect = getRectData(polygon)
                sb.append("${rect.x} ${rect.y} ${rect.width} ${rect.height} ")
            }
            sb.appendln()
        }

        Files.write(Paths.get(positivesFile.absolutePath), sb.toString().toByteArray())
    }

    fun resizeCanvas() {
        canvas!!.width = stage.width
        canvas!!.height = stage.height - 100.0
    }

    fun initImage() {
        infoText!!.text = "$imageCounter of ${annotationImages.size}"

        calculateScaleFactor()
        redrawCanvas()
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

    fun redrawCanvas() {
        gc.clearRect(0.0, 0.0, canvas!!.width, canvas!!.height)
        gc.drawImage(activeImage.image, 0.0, 0.0,
                activeImage.image.width * scaleFactor,
                activeImage.image.height * scaleFactor)

        gc.stroke = Color.LIGHTGREEN

        // draw polygons
        for (polygon in activeImage.polygons) {
            val rect = getRectData(polygon, scaleFactor)
            gc.strokeRect(rect.x, rect.y, rect.width, rect.height)
        }
    }

    fun handleKeyEvent(e: KeyEvent) {
        when (e.code) {
            KeyCode.SPACE -> nextClicked(ActionEvent())
            KeyCode.C -> clearClicked(ActionEvent())
        }
    }

    var dragged = false
    var dragStartPoint = Vector2.NULL
    var activePolygon = Polygon2()

    fun handleMouseDragged(e: MouseEvent) {
        val m = Vector2(e.x, e.y)

        activePolygon.points[1] = vectorToImage(m)
        dragged = true

        redrawCanvas()
    }

    fun handleMousePressed(e: MouseEvent) {
        val m = Vector2(e.x, e.y)

        activePolygon = Polygon2(vectorToImage(m), vectorToImage(m))
        activeImage.polygons.add(activePolygon)

        dragged = false
        dragStartPoint = m
    }

    fun handleMouseReleased(e: MouseEvent) {
        if (dragged) {
            println("dragged!")
        } else {
            activeImage.polygons.remove(activePolygon)
            println("clicked!")
        }
    }

    fun clearClicked(e: ActionEvent) {
        activeImage.polygons.clear()
        redrawCanvas()
    }

    fun nextClicked(e: ActionEvent) {
        loadNextImage()
    }

    fun finishClicked(e: ActionEvent) {
        saveAndClose()
    }

    fun vectorToScreen(v: Vector2): Vector2 {
        return v.scale(scaleFactor)
    }

    fun vectorToImage(v: Vector2): Vector2 {
        return v.scale(1.0 / scaleFactor)
    }

    fun getRectData(poly: Polygon2, scale: Double = 1.0): Rectangle {
        val x1 = if (poly[0].x < poly[1].x) poly[0].x else poly[1].x
        val y1 = if (poly[0].y < poly[1].y) poly[0].y else poly[1].y

        val x2 = if (poly[0].x > poly[1].x) poly[0].x else poly[1].x
        val y2 = if (poly[0].y > poly[1].y) poly[0].y else poly[1].y

        val width = x2 - x1
        val height = y2 - y1

        return Rectangle(x1 * scale, y1 * scale, width * scale, height * scale)
    }
}