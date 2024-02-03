package cda.view.jfxuserform;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import cda.view.helpers.Nodes;

/**
 * (almost) Empty stage with some default config and styling (material theme like) to make it look
 * "fancy". (has just a predefined scene and custom title/top bar )
 */
public class FancyStage extends Stage {
    // scale to respect aspect ratio & keep default proportions (i.e. 446x411)
    //private final SimpleDoubleProperty scaleX = new SimpleDoubleProperty(1.2), scaleY = new SimpleDoubleProperty(1.5);
    private final static float scaleX = 1.4f, scaleY = 1.5f;
    private final static double rootDefWidth = 446, rootDefHeight = 411;
    private final static String bgColor = "#2e354f";
    private final static String subrootStyle = String.format("-fx-background-color: %s;", bgColor);
    private final Scene scene;
    private final AnchorPane root;
    private final VBox subroot;
    private final VBox center;

    public FancyStage() {
        super(StageStyle.DECORATED);
        this.subroot = Nodes.setUpNewVBox(0, Pos.CENTER, true);
        this.center = defaultCenter();
        subroot.getChildren().add(center);

        subroot.setStyle(subrootStyle);

        this.root = new AnchorPane(subroot);

        root.setPrefSize(rootDefWidth * scaleX, rootDefHeight * scaleY);

        bindSizeToParent(subroot, root);

        this.scene = new Scene(root, Color.valueOf("2e354f"));
        this.setScene(scene);
        this.centerOnScreen();
    }

    public void resetSize() {
        this.setWidth(rootDefWidth * scaleX);
        this.setHeight(rootDefHeight * scaleY);
    }

    /**
     * Default center/ principal content node provider.
     *
     * @return Default Center Node, to be able to change view rapidly.
     * (Before we would just change scene but here since we cannot we just change center, the only non-final field)
     */
    public VBox defaultCenter() {
        VBox out = Nodes.setUpNewVBox(0, Pos.CENTER, true);
        out.prefHeightProperty().bind(subroot.heightProperty().multiply(0.99));
        return out;
    }

    public Scene scene() {
        return this.scene;
    }

    protected VBox subroot() {
        return this.subroot;
    }

    protected VBox center() {
        return this.center;
    }

    public static void bindSizeToParent(Region child, Region parent) {
        child.prefHeightProperty().bind(parent.heightProperty().subtract(0.3));
        child.minHeightProperty().bind(parent.heightProperty().multiply(0.99));
        child.prefWidthProperty().bind(parent.widthProperty());
        child.minWidthProperty().bind(parent.widthProperty().multiply(0.99));
    }
}

