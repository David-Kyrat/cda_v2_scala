package cda.view.jfxuserform;

import cda.view.helpers.Nodes;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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

    private BorderPane setupRoot() {
        return new BorderPane();
    }

    private MFXButton setupBackButton(FancyStage stage) {
        MFXButton back = ChoosingStage.createAbbrevBtn("Back");
        back.getStyleClass().add("signupLabel");
        Nodes.bindWidthToParent(back, root);
        back.setOnMouseClicked(me -> {
            tableView.cleanUp();
            me.consume();
            stage.setScene(previousScene);
            stage.resetSize();
            stage.centerOnScreen();
        });
        return back;
    }

    public void resetSize(FancyStage stage) {
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
        this.tableView = new JFXTableView(
                choosingStage.getAbbrevFileContent(),
                "Name",
                "Abbreviation");
        root.setCenter(tableView.get());
        this.backButton = setupBackButton(choosingStage);
        backButton.setRippleAnimationSpeed(
                backButton.getRippleAnimationSpeed() * 3);

        root.setBottom(this.backButton);
        this.abbrevScene = new Scene(root);
        abbrevScene
                .getStylesheets()
                .addAll(
                        "jfxuserform/jfoenix-components.css",
                        "jfxuserform/abbrevDisplayer.css");
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
                    // NOTE: Fake mouse event to make `backButton` ripple when user double click on
                    // a study plan => hence give backButton as `source` and `dest` of the
                    // mouseEvent
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
                            null);
                    backButton.getRippleGenerator().generateRipple(fakeMe);
                    choosingStage.addToSpSelection(selectedAbbrev);
                }
            }
            me.consume();
        });
    }

    public Scene getScene() {
        return abbrevScene;
    }
}
