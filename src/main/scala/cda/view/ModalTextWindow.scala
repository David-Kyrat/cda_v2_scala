package cda

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.Scene
import javafx.application.Platform
import javafx.scene.text.TextFlow
import cda.view.helpers.Nodes
import javafx.scene.text.TextAlignment
import javafx.geometry.Pos
import cda.view.jfxuserform.Main
import javafx.stage.StageStyle
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implements a modal window for displaying text.
 * Usefull when doing some cli operations or print error message and we want the user
 * to be able to see it in production.
 */
class ModalTextWindow(text: String) extends Application:
    // override def start(primaryStage: Stage): Unit =
    def start(): Unit =
        val text = "Your text\n here"
        val textFlow = new TextFlow(Nodes.newTxt(text, Color.BLACK, 24));
        textFlow.setTextAlignment(TextAlignment.CENTER)
        textFlow.setTabSize(8)
        textFlow.setLineSpacing(2.5)
        val root = Nodes.setUpNewVBox(20, 400, 400, Pos.CENTER, true, textFlow)
        val scene = new Scene(root, 400, 400)
        primaryStage.setScene(scene)
        Main.setStage(primaryStage)
        // primaryStage.show()
        // primaryStage.centerOnScreen()

    /* def startJavaFxRuntime() =
        Platform.startup(() => {
            super.init()
            val s = new Stage()
            val rect = new javafx.scene.shape.Rectangle(400, 400, Color.RED)
            val root = new VBox(rect)
            val scene = new Scene(root, 400, 400)
            s.setScene(scene)
            s.show()
            s.centerOnScreen()
        })
        while true do {} */

object ModalTextWindow:
    /* def launchApp(text: String): Unit = {
        // Initialize the JavaFX toolkit
        new Thread(() => Application.launch(classOf[ModalTextWindow], text)).start()

        // Wait for the JavaFX Application Thread to be ready
        Platform.runLater(() => {
            // Your application initialization logic here if needed
        })
    } */
    private var done = new AtomicBoolean(false)
    def isDone: Boolean = done.get

    /**
     * Starts a modal window for displaying text.
     * Usefull when doing some cli operations or print error message and we want the user
     * to be able to see it in production.
     *  When window gets a close request, it sets `ModalTextWindow.done` to `true`.
     *  Therefore you should check put a `while !ModalTextWindow.isDone do {}` after calling this method
     */
    def start(text: String): Unit =
        Platform.runLater(() =>
            val primaryStage = new Stage(StageStyle.DECORATED)
            val textFlow = new TextFlow(Nodes.newTxt(text, Color.BLACK, 24));
            textFlow.setTextAlignment(TextAlignment.CENTER)
            textFlow.setTabSize(8)
            textFlow.setLineSpacing(2.5)
            val root = Nodes.setUpNewVBox(20, 400, 400, Pos.CENTER, true, textFlow)
            val scene = new Scene(root, 400, 400)
            primaryStage.setScene(scene)
            Main.setStage(primaryStage)
            primaryStage.setOnCloseRequest(_ => done.set(true))
        )

    def main(args: Array[String]): Unit =
        Application.launch(classOf[ModalTextWindow], args: _*)
