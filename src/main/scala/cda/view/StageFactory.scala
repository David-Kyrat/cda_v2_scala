package cda.view

import javafx.stage.Stage
import javafx.stage.StageStyle

object StageFactory:
    def createStage(): Unit =
        println("Creating a new stage")

    /**
     * @return new Decorad Stage (i.e. `new Stage(StageStyle.DECORATED)`). 
     * NB: Must be called on JavaFx Thread !
     */
    def decoratedStage = new Stage(StageStyle.DECORATED)
    /**
     * @return new undecorated Stage (i.e. `new Stage(StageStyle.UNDECORATED)`). 
     * NB: Must be called on JavaFx Thread !
     */
    def undecoratedStage = new Stage(StageStyle.UNDECORATED)
    /**
     * @return new unified Stage (i.e. `new Stage(StageStyle.UNIFIED)`). 
     * NB: Must be called on JavaFx Thread !
     */
    def unfiedStage = new Stage(StageStyle.UNIFIED)
    /**
     * @return new Transparent Stage (i.e. `new Stage(StageStyle.TRANSPARENT)`). 
     * NB: Must be called on JavaFx Thread !
     */
    def transparentStage = new Stage(StageStyle.TRANSPARENT)
