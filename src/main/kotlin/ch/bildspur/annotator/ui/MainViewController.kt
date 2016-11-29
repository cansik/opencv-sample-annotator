package ch.bildspur.annotator.ui

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import kotlin.properties.Delegates

/**
 * Created by cansik on 29.11.16.
 */
class MainViewController {
    @FXML
    var imageView: ImageView? = null

    var positivesFile: File by Delegates.notNull()

    var datasetFiles: List<File> by Delegates.notNull()

    init {
    }

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
        datasetFiles = controller.datasetFiles!!
    }
}