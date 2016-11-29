package ch.bildspur.annotator.ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File


/**
 * Created by cansik on 29.11.16.
 */
class SettingsViewController {
    @FXML
    var datasetPathField: TextField? = null

    @FXML
    var positivesPathField: TextField? = null


    var positivesFile: File? = null

    var datasetFiles: List<File>? = null

    var isStart = false

    fun selectDataset(e: ActionEvent) {
        val stage = (e.source as Node).scene.window as Stage

        val fileChooser = FileChooser()
        fileChooser.title = "Select dataset"
        fileChooser.initialFileName = ""
        fileChooser.initialDirectory = File(System.getProperty("user.home"))
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("All Images", "*.*"),
                FileChooser.ExtensionFilter("JPG", "*.jpg"),
                FileChooser.ExtensionFilter("PNG", "*.png")
        )

        datasetFiles = fileChooser.showOpenMultipleDialog(stage)

        if (datasetFiles != null)
            datasetPathField!!.text = datasetFiles!!.joinToString { it.name }
    }

    fun selectPositives(e: ActionEvent) {
        val stage = (e.source as Node).scene.window as Stage

        val fileChooser = FileChooser()
        fileChooser.title = "Select file to save positives to:"
        fileChooser.initialFileName = "positives.txt"
        fileChooser.initialDirectory = File(System.getProperty("user.home"))
        positivesFile = fileChooser.showSaveDialog(stage)

        if (positivesFile != null)
            positivesPathField!!.text = positivesFile!!.absolutePath
    }

    fun startAnnotation(e: ActionEvent) {
        val stage = (e.source as Node).scene.window as Stage

        if (datasetFiles != null && positivesFile != null) {
            isStart = true
            stage.close()
        } else {
            val alert = Alert(AlertType.WARNING)
            alert.title = "Warning Dialog"
            alert.headerText = "Not all data provided!"
            alert.contentText = "Please fill out all the settings!"

            alert.showAndWait()
        }
    }
}