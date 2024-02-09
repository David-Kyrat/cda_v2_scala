package cda

import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

import java.util.concurrent.CountDownLatch
import javafx.scene.text.TextFlow
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.geometry.Pos
import cda.view.helpers.Nodes
import javafx.scene.paint.Paint
import cda.view.StageFactory

object JavaFXInitializer {

    // Latch that waits for the JavaFX application to terminate
    private val latch = new CountDownLatch(1)

    def initializeJavaFXToolkit(): Unit = {
        // Initialize the JavaFX Toolkit
        Platform.startup(() => {
            // Toolkit initialization logic goes here, if any.
            // Decrement the latch count when the JavaFX application is about to terminate
        })
    }

    def createAndShowGUI(text: String): Unit = {
        // Ensure that this method is run on the JavaFX Application Thread
        Platform.runLater(() => {
            val primaryStage = StageFactory.decoratedStage
            val textFlow = new TextFlow(Nodes.newTxt(text, Color.BLACK, 24));
            textFlow.setTextAlignment(TextAlignment.CENTER)
            textFlow.setTabSize(8)
            textFlow.setLineSpacing(2.5)
            

            val root = Nodes.setUpNewVBox(20, 400, 400, Pos.CENTER, true, textFlow)
            val scene = new Scene(root, 400, 400)
            primaryStage.setScene(scene)
            primaryStage.show()
            primaryStage.centerOnScreen()

            // Close handler for primaryStage that decrements the latch,
            // allowing the application to terminate gracefully when the window is closed
            primaryStage.setOnCloseRequest(_ => latch.countDown())
        })
    }

    def main(args: Array[String]): Unit = {
        // Initialize the JavaFX Toolkit
        initializeJavaFXToolkit()

        // Create and show the GUI
        createAndShowGUI("Your text\n here")

        // Wait for the JavaFX application to terminate
        latch.await()
        println("JavaFXInitializer  has terminated")
        // Platform.exit() // Ensure JavaFX platform exits cleanly
    }
}
