package ch.bildspur.annotator

import ch.bildspur.annotator.ui.MainViewController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.WindowEvent


/**
 * Created by cansik on 29.11.16.
 */
class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {

        val loader = FXMLLoader()
        val root = loader.load<Parent>(javaClass.classLoader.getResourceAsStream("view/MainView.fxml"))

        val controller = loader.getController<MainViewController>()
        controller.stage = primaryStage
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN) { controller.handleWindowShownEvent() }

        primaryStage.title = "OpenCV Sample Annotator"
        primaryStage.scene = Scene(root)
        primaryStage.show()
        System.out.println()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}