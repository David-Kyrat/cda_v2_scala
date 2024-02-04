package cda.view.jfxuserform;

import cda.view.helpers.Nodes;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public final class AbbrevDisplayer {

    private static final float scaleX = 1.4f, scaleY = 1.1f;
    private static final double rootDefWidth = 981.2, rootDefHeight = 615.5;
    private final double baseWidth, baseHeight;
    private final ChoosingStage choosingStage;
    private final JFXTableView tableView;
    private final Scene previousScene;
    private final Scene abbrevScene;
    private final BorderPane root;
    private final MFXButton backButton;
    private final MFXCircleRippleGenerator rippler;

    private BorderPane setupRoot() {
        /* BorderPane r = new BorderPane(); */
        // r.setPrefSize(500, 700);
        return new BorderPane();
    }

    private MFXButton setupBackButton(FancyStage stage) {
        MFXButton back = ChoosingStage.createAbbrevBtn("Back");
        back.getStyleClass().add("signupLabel");
        Nodes.bindWidthToParent(back, root);
        back.setOnAction(e -> {
            stage.setScene(previousScene);
            stage.resetSize();
            stage.centerOnScreen();
        });
        return back;
    }

    public void resetSize(FancyStage stage) {
        /*
         * stage.setWidth(rootDefWidth * scaleX);
         * stage.setHeight(rootDefHeight * scaleY);
         */
        /*
         * root.setMinWidth(rootDefHeight * scaleX * 1.5);
         * root.setPrefHeight(rootDefHeight * scaleY);
         */
        root.setMinWidth(baseWidth * scaleX);
        root.setPrefHeight(baseHeight * scaleY);
        stage.sizeToScene();
    }

    public AbbrevDisplayer(ChoosingStage choosingStage) {
        this.baseWidth = choosingStage.getWidth();
        this.baseHeight = choosingStage.getHeight();
        this.previousScene = choosingStage.getScene();
        this.root = setupRoot();
        this.choosingStage = choosingStage;
        this.tableView =
            new JFXTableView(
                choosingStage.getAbbrevFileContent(),
                "Name",
                "Abbreviation"
            );
        root.setCenter(tableView.get());
        this.backButton = setupBackButton(choosingStage);
        // this.rippler = new JFXRippler(backButton);
        this.rippler = new MFXCircleRippleGenerator(backButton);
        // this.rippler.setAnimateShadow(true);
        // this.rippler.setAutoClip(false);
        backButton.setRippleAnimationSpeed(backButton.getRippleAnimationSpeed()*3);

        root.setBottom(this.backButton);
        this.abbrevScene = new Scene(root);
        abbrevScene
            .getStylesheets()
            .addAll("jfxuserform/jfoenix-components.css");
        resetSize(choosingStage);
        handleCopyOnclick();
    }

    static void copyToClipboard(String content) {
        StringSelection stringSelection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * Handles the onclick event of the abbreviation listview.
     * i.e. when the user clicks on an abbreviation, it is directly copied to the
     * clipboard.
     * if user double clicks => adds study plan to selection.
     */
    private void handleCopyOnclick() {
        tableView.setOnMouseClicked(me -> {
            String selectedAbbrev = tableView.getSelectedAbbreviation();
            copyToClipboard(selectedAbbrev);
            if (me.getButton().equals(MouseButton.PRIMARY)) {
                if (me.getClickCount() >= 2) {
                    // choosingStage.addToStudyPlanSelection(selectedAbbrev);
                    // rippler.generateRipple(me);
                    // NOTE: Fake mouse event to make `backButton` ripple when user double click on
                    // a study plan
                    // => hence give backButton as `source` and `dest` of the mouseEvent
                    MouseEvent fakeMe = new MouseEvent(
                        backButton,
                        backButton,
                        MouseEvent.ANY,
                        0,
                        0,
                        0,
                        0,
                        MouseButton.NONE,
                        2,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        null
                    );
                    backButton.getRippleGenerator().generateRipple(fakeMe);
                    choosingStage.addToSpSelection(selectedAbbrev);
                    // var r = rippler.createManualRipple();
                    // r.run();
                    // rippler.generateRipple(me);
                }
            }
            me.consume();
        });
    }

    public Scene getScene() {
        return abbrevScene;
    }
}
