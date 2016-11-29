package ch.bildspur.annotator

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Created by cansik on 29.11.16.
 */
class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<Parent>(javaClass.classLoader.getResource("view/MainView.fxml"))
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