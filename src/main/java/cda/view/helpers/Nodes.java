package cda.view.helpers;

import java.util.List;
import java.util.function.Consumer;

import javafx.css.Styleable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import cda.view.jfxuserform.ChoosingStage;

/**
 * This class contains frequently used methods that are adapted in order to make the code more clear
 * @author Noah Munz
 */
@SuppressWarnings("unused") // suppress warning because it's a utility class for potential existing or upcoming changes
public final class Nodes {

    private Nodes() {
        throw new UnsupportedOperationException();
    }

    /**
     * "Add a class to the style class list of the given styleable."
     *
     * @param s The Styleable object you want to add the class to.
     * @param className The name of the class to add.
     */
    public static void addClass(Styleable s, String... className) {
        s.getStyleClass().addAll(className);
    }

    public static void bindSizeToParent(Region child, Region parent) {
        bindWidthToParent(child, parent);
        bindHeightToParent(child, parent);
    }

    public static void bindWidthToParent(Region child, Region parent) {
        child.prefWidthProperty().bind(parent.widthProperty());
        child.minWidthProperty().bind(parent.widthProperty().multiply(0.99));
    }

    public static void bindHeightToParent(Region child, Region parent) {
        child.prefHeightProperty().bind(parent.heightProperty());
        child.minHeightProperty().bind(parent.heightProperty().multiply(0.99));
    }

    /**
     * Sets the layoutX and layoutY properties of the given node to the given values.
     *
     * @param node The node to be positioned.
     * @param layoutX The x coordinate of the upper left corner of the layout bounds of the node.
     * @param layoutY The y coordinate of the upper left corner of the layout bounds.
     */
    public static void setLayout(Node node, double layoutX, double layoutY) {
        node.setLayoutX(layoutX); node.setLayoutY(layoutY);
    }

    /**
     * Set the layoutX, layoutY, prefWidth, and prefHeight properties of the given Region.
     *
     * @param region the region to set the layout and pref size for
     * @param layoutX The x coordinate of the upper left corner of the region.
     * @param layoutY The vertical position of the top of the region.
     * @param prefW The preferred width of the region.
     * @param prefH The preferred height of the region.
     */
    public static void setLayoutAndPrefSize(Region region, double layoutX, double layoutY, double prefW, double prefH) {
        region.setLayoutX(layoutX); region.setLayoutY(layoutY);
        region.setPrefSize(prefW, prefH);
    }

    /**
     * @param <E> E, Type of Node subclass
     * @param nodeToPerformActionOn E extends Node
     * @param actionPerformer Consumer of E
     * @return Node after action performed
     */
    public static <E extends Node> E withAction(E nodeToPerformActionOn, Consumer<E> actionPerformer) {
        actionPerformer.accept(nodeToPerformActionOn);
        return nodeToPerformActionOn;
    }

    /**
     * Adds all the given children to the given Parent and then returns the parent
     * @param parent   Node to add the children to
     * @param children Node to put in <code>parent</code>
     * @param <E>      Type of the Parent
     * @return Given Parent with added Children
     */
    public static <E extends Pane, T extends Node> E withChildren(E parent, List<T> children) {
        parent.getChildren().addAll(children);
        return parent;
    }

    /**
     * Adds all the given children to the given Parent and then returns the parent
     * @param parent   Node to add the children to
     * @param children List of Node to put in <code>parent</code>
     * @param <E>      Type of the Parent
     * @return Given Parent with added Children
     */
    public static <E extends Group, T extends Node> E withChildren(E parent, List<T> children) {
        parent.getChildren().addAll(children);
        return parent;
    }

    /**
     * Adds all the given children to the given Parent and then returns the parent
     * @param parent   Node to add the children to
     * @param children Nodes to put in <code>parent</code>
     * @param <E>      Type of the Parent
     * @return Given Parent with added Children
     */
    public static <E extends Pane> E withChildren(E parent, Node... children) {
        parent.getChildren().addAll(children);
        return parent;
    }

    /**
     * Adds all the given children to the given Parent and then returns the parent
     * @param parent   Node to add the children to
     * @param children Nodes to put in <code>parent</code>
     * @param <E>      Type of the Parent
     * @return Given Parent with added Children
     */
    public static <E extends Group> E withChildren(E parent, Node... children) {
        parent.getChildren().addAll(children);
        return parent;
    }

    /**
     * Adds all the given CSS classes to the given node and then returns it
     * @param node       Node to add the classes to
     * @param classNames All the classes to add to <code>node</code>
     * @param <E>        Type of <code>node</code>
     * @return Given node with added classes
     */
    public static <E extends Node> E withClass(E node, String... classNames) {
        node.getStyleClass().addAll(classNames);
        return node;
    }

    /**
     * Short auxiliary method to set the scene of a Stage and show the stage
     * @param stage Stage
     * @param scene Scene
     */
    public static void setShow(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Short auxiliary method to set the scene of a Stage, Show the stage and centers it on the Screen
     * and set <code>stage.fullScreen()</code> to fullscreen
     * @param stage      Stage
     * @param scene      Scene
     * @param fullscreen whether given stage should be displayed on the entire screen
     */
    public static void setShowCenter(Stage stage, Scene scene, boolean fullscreen) {
        setShow(stage, scene);
        stage.centerOnScreen();
        stage.setFullScreen(fullscreen);
    }

    public static void showCenter(Stage stage, boolean fullscreen) {
        stage.show();
        stage.centerOnScreen();
        stage.setFullScreen(fullscreen);
    }

    /**
     * Short auxiliary method to set the scene of a Stage, Show the stage and centers it on the Screen
     * @param stage Stage
     * @param scene Scene
     */
    public static void setShowCenter(Stage stage, Scene scene) {
        setShowCenter(stage, scene, false);
    }

    /**
     * Creates an instance of VBox and set it Up with following parameters and centers it
     * @param elementSpacing double
     * @param prefWidth      double
     * @param prefHeight     double
     * @param pos            geometry.pos (enum)
     * @param fillWidth      boolean
     * @param children       Nodes to add to the VBox
     * @return Vbox centered
     */
    public static VBox setUpNewVBox(double elementSpacing, double prefWidth, double prefHeight, Pos pos, boolean fillWidth, Node... children ) {
        VBox output = (VBox) setUpNewPane(new VBox(elementSpacing), prefWidth, prefHeight);
        output.setAlignment(pos);
        output.setFillWidth(fillWidth);
        output.getChildren().addAll(children);
        return output;
    }

    /**
     * Creates an instance of VBox and set it Up with following parameters and centers it
     * @param elementSpacing double
     * @param pos            geometry.pos (enum)
     * @param fillWidth      boolean
     * @return Vbox centered
     */
    public static VBox setUpNewVBox(double elementSpacing, Pos pos, boolean fillWidth) {
        VBox output = new VBox(elementSpacing);
        output.setAlignment(pos);
        output.setFillWidth(fillWidth);
        return output;
    }

    /**
     * Creates an instance of VBox and set it Up with following parameters, centers it and adds the given children to it
     * @param elementSpacing double
     * @param pos            geometry.pos (enum)
     * @param fillWidth      boolean
     * @param children       Nodes to add to the VBox
     * @return Vbox centered
     */
    public static VBox setUpNewVBox(double elementSpacing, Pos pos, boolean fillWidth, Node... children) {
        VBox output = new VBox(elementSpacing);
        output.setAlignment(pos);
        output.setFillWidth(fillWidth);
        output.getChildren().addAll(children);
        return output;
    }

    /**
     * Creates an instance of VBox and set it Up with following parameters and centers it
     * @param elementSpacing double
     * @param pos            geometry.pos (enum)
     * @param fillHeight     boolean
     * @return Hbox
     */
    public static HBox setUpNewHBox(double elementSpacing, Pos pos, boolean fillHeight) {
        HBox output = new HBox(elementSpacing);
        output.setAlignment(pos);
        output.setFillHeight(fillHeight);
        return output;
    }

    /**
     * Creates an instance of VBox and set it Up with following parameters, centers it and adds the given children to it
     * @param elementSpacing double
     * @param pos            geometry.pos (enum)
     * @param fillHeight     boolean
     * @param children       Nodes to add to the HBox
     * @return Hbox
     */
    public static HBox setUpNewHBox(double elementSpacing, Pos pos, boolean fillHeight, Node... children) {
        HBox output = new HBox(elementSpacing);
        output.setAlignment(pos);
        output.setFillHeight(fillHeight);
        output.getChildren().addAll(children);
        return output;
    }

    /**
     * Set up a new Pane and set its prefWidth and prefHeight to the given value
     * @param pane       Pane
     * @param prefWidth  double
     * @param prefHeight double
     * @return pane
     */
    public static Pane setUpNewPane(Pane pane, double prefWidth, double prefHeight) {
        pane.setPrefSize(prefWidth, prefHeight);
        return pane;
    }

    /**
     * @param message  String
     * @param color    Color
     * @param fontsize int
     * @return new Text()
     */
    public static Text newTxt(String message, Color color, int fontsize) {
        Text t1 = new Text(message);
        styleTxt(t1, t1.getFont().getName(), fontsize, FontWeight.MEDIUM);
        t1.setFill(color);
        return t1;
    }
       
    /**
     * @param fontWeight as int (i.e. 200, 400, 600, 800, 1000)
     * @return Matching FontWeight
     */
    public static FontWeight int2FontWeight(int fontWeight) {
        return switch (fontWeight) {
            case 200 -> FontWeight.LIGHT;
            case 600 -> FontWeight.SEMI_BOLD;
            case 800 -> FontWeight.BOLD;
            case 1000 -> FontWeight.BLACK;
            default -> FontWeight.MEDIUM;
        };
    }

    /**
     * @param message  String
     * @param color    Color
     * @param fontsize int
     * @param fontWeight FontWeight
     * @return new Text()
     */
    public static Text newTxt(String message, Color color, int fontsize, int fontWeight) {
        Text t1 = new Text(message);
        styleTxt(t1, t1.getFont().getName(), fontsize, int2FontWeight(fontWeight));
        t1.setFill(color);
        return t1;
    }

    /**
     * @param message  String
     * @param font     String
     * @param color    Color
     * @param fontsize int
     * @return new Text()
     */
    public static Text newTxt(String message, String font, Color color, int fontsize) {
        Text t1 = new Text(message);
        styleTxt(t1, font, fontsize, FontWeight.MEDIUM);
        t1.setFill(color);
        return t1;
    }

    /**
     * @param message    String
     * @param font       String
     * @param color      Color
     * @param fontsize   int
     * @param fontWeight int
     * @return new Text()
     */
    public static Text newTxt(String message, String font, Color color, int fontsize, int fontWeight) {
        Text t1 = new Text(message);
        styleTxt(t1, font, fontsize, int2FontWeight(fontWeight));
        t1.setFill(color);
        return t1;
    }

    /**
     * @param t          Text
     * @param font       String
     * @param fontSize   int
     * @param fontWeight FontWeight
     */
    public static void styleTxt(Text t, String font, int fontSize, FontWeight fontWeight) {
        t.setFont(Font.font(font, fontWeight, FontPosture.REGULAR, fontSize));
    }

    public static void addStyleSheet(Scene scene, String fileName) {
        scene.getStylesheets().add(ChoosingStage.class.getResource(fileName).toExternalForm());}

    /**
     * Create a textflow from a list of text
     * @param texts          List of Text
     */
    public static TextFlow newTextFlow(List<Text> texts) {
        return withAction(new TextFlow(), tf -> tf.getChildren().addAll(texts));
    }

    /**
     * Adds debug css stylesheet to given scene
     * @param scene scene to add StyleSheet to
     */
    public static void addDebug(Scene scene) {
        /* String absolutePath = "C:/Users/noahm/OneDrive/Prog-Projects/Java-Scala/JavaFx-loginForm-maven/JavaFX-LoginForm1/src/loginform/debug.css";*/
        String fileName = "debug.css";
        //scene.getStylesheets().add(ch.view.jfxuserform.Main.class.getResource("debug.css").toExternalForm());
        addStyleSheet(scene, fileName);
    }

    public static <E extends Region> void bindPrefSizeWithStage(Stage stage, E nodeToResize, double divideFactorW, double divideFactorH) {
        nodeToResize.prefWidthProperty().bind(stage.widthProperty().divide(divideFactorW));
        nodeToResize.prefHeightProperty().bind(stage.heightProperty().divide(divideFactorH));
    }

    public static void setStageMinSize(Stage stage, double minWidth, double minHeight) {
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
    }

    public static void setStageMaxSize(Stage stage, double maxWidth, double maxHeight) {
        stage.setMaxWidth(maxWidth);
        stage.setMaxHeight(maxHeight);
    }

}
