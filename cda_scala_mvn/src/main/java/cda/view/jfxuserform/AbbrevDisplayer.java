package cda.view.jfxuserform;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import cda.view.helpers.Nodes;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;


public final class AbbrevDisplayer {
    private final static float scaleX = 1.4f, scaleY = 1.1f;
    private final static double rootDefWidth = 981.2, rootDefHeight = 615.5;
    private final double baseWidth, baseHeight;
    private final ChoosingStage choosingStage;
    private final JFXTableView tableView;
    private final Scene previousScene;
    private final Scene abbrevScene;
    private final BorderPane root;
    private final JFXButton backButton;
    private final JFXRippler rippler;

    private BorderPane setupRoot() {
        /*BorderPane r = new BorderPane();*/
        //r.setPrefSize(500, 700);
        return new BorderPane();
    }

    private JFXButton setupBackButton(FancyStage stage) {
        JFXButton back = ChoosingStage.createAbbrevBtn("Back");
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
        /*stage.setWidth(rootDefWidth * scaleX);
        stage.setHeight(rootDefHeight * scaleY);*/
        /*root.setMinWidth(rootDefHeight * scaleX * 1.5);
        root.setPrefHeight(rootDefHeight * scaleY);*/
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
        this.tableView = new JFXTableView(choosingStage.getAbbrevFileContent(), "Name", "Abbreviation");
        root.setCenter(tableView.get());
        this.backButton = setupBackButton(choosingStage);
        this.rippler = new JFXRippler(backButton);
        root.setBottom(rippler);
        this.abbrevScene = new Scene(root);
        abbrevScene.getStylesheets().addAll("jfxuserform/jfoenix-components.css");
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
     * i.e. when the user clicks on an abbreviation, it is directly copied to the clipboard.
     * if user double clicks => adds study plan to selection.
     */
    private void handleCopyOnclick() {
        tableView.setOnMouseClicked(me -> {
            String selectedAbbrev = tableView.getSelectedAbbreviation();
            copyToClipboard(selectedAbbrev);
            if (me.getButton().equals(MouseButton.PRIMARY)) {
                if (me.getClickCount() >= 2) {
                    //choosingStage.addToStudyPlanSelection(selectedAbbrev);
                    choosingStage.addToSpSelection(selectedAbbrev);
                    var r = rippler.createManualRipple();
                    r.run();
                }
            }
            me.consume();
        });
    }

    public Scene getScene() {
        return abbrevScene;
    }
}
