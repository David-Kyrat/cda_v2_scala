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
// class ModalTextWindow(text: String, width: Int = 600, height: Int = 600, fontSize: Int = 24):
class ModalTextWindow(textFlow: TextFlow, width: Int = 600, height: Int = 600, title: String = ""):
    // override def start(primaryStage: Stage): Unit =
    private val done = new AtomicBoolean(false)
    def isDone: Boolean = done.get

    /**
     * Starts a modal window for displaying text.
     * Usefull when doing some cli operations or print error message and we want the user
     * to be able to see it in production.
     *  When window gets a close request, it sets `ModalTextWindow.done` to `true`.
     *  Therefore you should check put a `while !modalTextWindow.isDone do {}` after calling this method
     *  @return `this` to be able to do
     *  ```scala
     *      val modalTextWindow = ModalTextWindow("Some \n new \n Text.").start()
     *      while !modalTextWindow.isDone do {}
     * ```
     */
    def start(): ModalTextWindow =
        Platform.runLater(() =>
            val primaryStage = StageFactory.decoratedStage
            if !title.strip.isBlank then primaryStage.setTitle(title)
            // val textFlow = new TextFlow(Nodes.newTxt(text, Color.BLACK, fontSize));
            textFlow.setTextAlignment(TextAlignment.LEFT)
            textFlow.setTabSize(8)
            textFlow.setLineSpacing(2.5)
            val root = Nodes.setUpNewVBox(0, width, height, Pos.CENTER, false, textFlow)
            val scene = new Scene(root, width, height)
            primaryStage.setScene(scene)
            Main.setStage(primaryStage)
            primaryStage.setOnCloseRequest(_ => done.set(true))
        )
        this

    /**
     * Starts a modal window for displaying text and blocks while user didn't close it.
     * Usefull when doing some cli operations or print error message and we want the user
     * to be able to see it in production.
     *  When window gets a close request, it sets `ModalTextWindow.done` to `true`.
     *  Therefore you should check put a `while !modalTextWindow.isDone do {}` after calling this method
     */
    def startAndBlock(): Unit =
        this.start()
        this.waitDone()

    /**
     * Wait (while loop) until `this.isDone` is true
     */
    def waitDone(): Unit =
        while !this.isDone do {}
