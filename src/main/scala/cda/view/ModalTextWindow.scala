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
import cda.view.StageFactory

/**
 * Implements a modal window for displaying text.
 * Usefull when doing some cli operations or print error message and we want the user
 * to be able to see it in production.
 */
// class ModalTextWindow(text: String) extends Application:
class ModalTextWindow(text: String):
    // override def start(primaryStage: Stage): Unit =
    private val done = new AtomicBoolean(false)
    def isDone: Boolean = done.get

    /**
     * Starts a modal window for displaying text.
     * Usefull when doing some cli operations or print error message and we want the user
     * to be able to see it in production.
     *  When window gets a close request, it sets `ModalTextWindow.done` to `true`.
     *  Therefore you should check put a `while !ModalTextWindow.isDone do {}` after calling this method
     */
    def start(): Unit =
        val primaryStage = StageFactory.decoratedStage
        val text = "Your text\n here"
        val textFlow = new TextFlow(Nodes.newTxt(text, Color.BLACK, 24));
        textFlow.setTextAlignment(TextAlignment.CENTER)
        textFlow.setTabSize(8)
        textFlow.setLineSpacing(2.5)
        val root = Nodes.setUpNewVBox(20, 400, 400, Pos.CENTER, true, textFlow)
        val scene = new Scene(root, 400, 400)
        primaryStage.setScene(scene)
        Main.setStage(primaryStage)


object ModalTextWindow:
    private val done = new AtomicBoolean(false)
    def isDone: Boolean = done.get

    /**
     * Starts a modal window for displaying text.
     * Usefull when doing some cli operations or print error message and we want the user
     * to be able to see it in production.
     *  When window gets a close request, it sets `ModalTextWindow.done` to `true`.
     *  Therefore you should check put a `while !ModalTextWindow.isDone do {}` after calling this method
     *  @param - text to display
     */
    def start(text: String): Unit =
        Platform.runLater(() =>
            val primaryStage = StageFactory.decoratedStage
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

    // def main(args: Array[String]): Unit =
    //     Application.launch(classOf[ModalTextWindow], args: _*)
