package cda.view.jfxuserform;

import static cda.view.helpers.Nodes.addClass;
import static javafx.scene.paint.Color.WHITE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import cda.view.helpers.Nodes;
import cda.view.jfxuserform.utilities.Pair;
import cda.view.jfxuserform.utilities.Quintuple;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * GUI implementation of ChoosingForm
 */
public final class ChoosingStage extends FancyStage {
    private final static double topLblPrefW = 200, topLblPrefH = 27;
    private final static String topLblText = """
            Enter one ore more course-code separated by a comma ',' to generate their pdf.
            To generate all the course in a  study plan, enter their abbreviation (or id if you have them) in the bottom field, and press Add.
            When done, click on 'generate' the generation will begin.""";
    private final static double usrLayoutX = 70, usrLayoutY = 123, usrPrefW = 264, usrPrefH = 35;
    private final static double pwdLayoutX = usrLayoutX, pwdLayoutY = 207, pwdPrefW = usrPrefW, pwdPrefH = usrPrefH,
                                mailLayoutY = 287;
    private final static double btnLayoutX = 115, btnLayoutY = 294, btnPrefW = 69, btnPrefH = 50;
    private final static String usrPromptTxt = "Course code", pwdPromptTxt = "Study-Plan Abbreviations",
                                btnLoginText = "Generate";
    private final static Color focusColor = WHITE, unfocusColor = WHITE;

    /**
     * root
     */
    private final BorderPane loginBp;
    private final VBox loginFieldsCtnr;
    private final JFXTextField courseField;
    private final JFXTextField studyPlanField;
    private final JFXButton generateBtn;
    private JFXButton abbrevBtn;
    private final JFXButton addBtn;

    private HBox courseSelectionViewCtnr;

    /**
     * Gets updated each time user click on "add" button
     */
    private final StringProperty courseSelectionViewValue;

    private final String spSelectionPropLabel = "Study Plan selection: ";
    private final StringProperty spSelectionViewValue;
    private final TextFlow userInputSelectionView;

    private final Set<String> courseSelectionSet;
    private final Set<String> spSelectionSet;

    private final String abbrevFilePath;
    private final List<String[]> abbrevFileContent;
    private AbbrevDisplayer ad;
    private String serializedOutput;

    /** @return Content to the file `abbrev.tsv` that will be displayed in the abbrevDisplayer */
    List<String[]> getAbbrevFileContent() { return abbrevFileContent; }

    /**
     * Primary constructor of LoginStage that generates the default login Stage
     * @param abbrevFilePath String path to the abbreviation file
     */
    public ChoosingStage(String abbrevFilePath) {
        super();
        this.serializedOutput = null;
        this.abbrevFilePath = abbrevFilePath;
        var tmp = createChoosingForm();
        this.loginBp = tmp._1();
        this.loginFieldsCtnr = tmp._2();
        this.courseField = tmp._3();
        this.studyPlanField = tmp._4();
        this.generateBtn = tmp._5();
        this.abbrevBtn = (JFXButton)((Parent)loginBp.getBottom()).getChildrenUnmodifiable().get(0);
        this.subroot().getChildren().add(loginBp);

        this.courseSelectionSet = new HashSet<>();
        this.spSelectionSet = new HashSet<>();

        this.courseSelectionViewValue = new SimpleStringProperty("Course selection: ");
        this.spSelectionViewValue = new SimpleStringProperty(spSelectionPropLabel);
        Text courseSelectionView = Nodes.newTxt("", WHITE, 16);
        Text spSelectionView = Nodes.newTxt("", WHITE, 16);
        this.userInputSelectionView =
            new TextFlow(courseSelectionView, new Text("\n"), spSelectionView, new Text("\n"));

        this.addBtn = new JFXButton("Add");
        addBtn.setButtonType(JFXButton.ButtonType.FLAT);
        addBtn.setPrefSize(100, 30);
        addBtn.getStyleClass().add("addBtn");
        addBtn.setOnMouseClicked(e -> handleAddBtn());

        this.courseSelectionViewCtnr = Nodes.setUpNewHBox(100, Pos.CENTER_LEFT, true, addBtn, userInputSelectionView);
        addClass(courseSelectionViewCtnr, "courseSelectionViewCtnr");
        HBox.setMargin(courseSelectionViewCtnr, new Insets(0, 0, 0, 20));
        courseSelectionViewCtnr.prefWidthProperty().bind(loginFieldsCtnr.widthProperty());

        courseSelectionView.textProperty().bind(courseSelectionViewValue);
        spSelectionView.textProperty().bind(spSelectionViewValue);

        /*loginBtn.setOnAction(e -> { handleAddBtn(); e.consume(); });*/
        addBtn.setOnAction(e -> {
            handleAddBtn();
            e.consume();
        });
        courseField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) studyPlanField.requestFocus();
            e.consume();
        });
        studyPlanField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) addBtn.requestFocus();
            addBtn.fireEvent(e);
            e.consume();
        });
        generateBtn.setOnMouseClicked(e -> {
            handleGenerateBtn();
            e.consume();
        });

        abbrevFileContent = extractPairsFromTsv();
        this.ad = new AbbrevDisplayer(this);

        // JavaFx Application should exit when this window is closed without clicking on generate button
        this.onCloseRequestProperty().setValue(v -> {
            cda.view.jfxuserform.Main.silenceBurningWaveLogsAfterStageClose();
            System.exit(0);
        });
    }

    /**
     * Wrapper for what would be done in the ch.view.jfxuserform.Main class.
     * i.e. show the stage, setup the form, center it on screen and size it to its content
     * as well as handling other visual setups that are required to be performed after stage has been showed
     */
    public void startAndShow() {
        this.scene().getStylesheets().add("/jfxuserform/login.css");
        this.show();
        this.setupChoosingFormAfterShow();
        this.centerOnScreen();
        this.sizeToScene();
    }

    /**
     * @return Serialized output to give to scala `Model.Main.main()` or null if user didn't choose yet.
     */
    public String getSerializedOutput() { return serializedOutput; }

    /**
     * Auxiliary constructor that calls the primary one,
     * then just set the title for this stage
     *
     * @param title Stage title
     * @param abbrevFilePath String path to the abbreviation file
     */
    public ChoosingStage(String title, String abbrevFilePath) {
        this(abbrevFilePath);
        this.setTitle(title);
    }

    /**
     * Visual setups that are required to be performed after stage has been showed
     */
    public void setupChoosingFormAfterShow() {
        float factor = 0.4f;
        float maxFactor = 0.6f;
        courseField.minWidthProperty().bind(loginFieldsCtnr.widthProperty().multiply(factor));
        studyPlanField.minWidthProperty().bind(loginFieldsCtnr.widthProperty().multiply(factor));
        courseField.maxWidthProperty().bind(loginFieldsCtnr.widthProperty().multiply(maxFactor));
        studyPlanField.maxWidthProperty().bind(loginFieldsCtnr.widthProperty().multiply(maxFactor));
        loginBp.minHeightProperty().bind(subroot().heightProperty().subtract(0));
        loginFieldsCtnr.getChildren().add(courseSelectionViewCtnr);
    }

    /**
     * Returns a pair of strings, the first being the username and the second being the hash of the
     * password
     *
     * @return LoginInfos as pair of strings.
     */
    private Pair<String, String> retrieveInput() {
        String usr = courseField.getText(), pwd = studyPlanField.getText();
        if (usr.isBlank() && pwd.isBlank()) return null;
        courseField.clear();
        studyPlanField.clear();
        return new Pair<>(usr, pwd);
    }

    public String serializeOutput(List<String> datas, List<String> titles) {
        if (datas == null || datas.isEmpty()) return "";
        if (titles == null || titles.size() < datas.size()) return String.join("#", datas);
        BinaryOperator<String> formatter = (title, data) -> title + ":" + data + "#";

        var itd = datas.iterator();
        var itt = titles.iterator();
        // StringBuilder sb = new StringBuilder(itt.next() + ":" + itd.next() + "#");
        StringBuilder sb = new StringBuilder(formatter.apply(itt.next(), itd.next()));
        while (itd.hasNext() && itt.hasNext()) sb.append(formatter.apply(itt.next(), itd.next()));
        return sb.toString();
    }

    /**
     * Adds the given study plan abbreviation to the current selection
     * (if it is not already present)
     *
     * @param _data abbreviation to add
     */
    void addToSpSelection(String _data) { addToSelection(_data, spSelectionViewValue, spSelectionSet); }

    private void addToSelection(String _data, StringProperty spSelectionViewValue, Set<String> spSelectionSet) {
        String data = _data.strip();
        if (data == null || data.isBlank()) return;
        String current = spSelectionViewValue.get();
        String sep = current.endsWith(": ") ? " " : ", ";
        spSelectionSet.add(data);
        spSelectionViewValue.set(current + sep + data);
    }

    private void addToUserSelection(String _data) {
        addToSelection(_data, courseSelectionViewValue, courseSelectionSet);
    }

    /**
     * Retrieves the login information from the login fields, and if they are not empty, prints them
     * to the console and calls the {@code login()} function
     */
    private void handleAddBtn() {
        Pair<String, String> infos = retrieveInput();
        if (infos == null) return;
        addToUserSelection(infos._1());
        addToSpSelection(infos._2());
    }

    private void handleGenerateBtn() {
        if (courseSelectionSet.isEmpty() && spSelectionSet.isEmpty()) return;
        String courses = String.join(",", courseSelectionSet);
        //System.out.println(courses);
        String sp = String.join(",", spSelectionSet);
        //System.out.println(sp);
        List<String> whole = List.of(courses, sp);
        this.serializedOutput = serializeOutput(whole, null);
        //System.out.println(serializedOutput);
        Platform.exit();
    }

    /* public void debug_signupScreen() { signupScreen(); }*/

    private List<String[]> extractPairsFromTsv() {
        try {
            return Files.lines(Path.of(abbrevFilePath)).parallel().map(line -> line.split("\t")).toList();
        } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
    }

    /**
     * Handle what happens when user clicks on 1st button sign up. i.e. the one that will ask for
     * user to create an account. Not the one to validate the created account info.
     */
    private void signupScreen() {
        ad = new AbbrevDisplayer(this);
        this.setScene(ad.getScene());
        ad.resetSize(this);
        this.centerOnScreen();
    }

    static JFXButton setupAcceptBtn(String text) {
        JFXButton acceptBtn = new JFXButton();
        acceptBtn.setText(text);
        acceptBtn.setTextAlignment(TextAlignment.CENTER);
        Nodes.setLayoutAndPrefSize(acceptBtn, btnLayoutX, btnLayoutY, btnPrefW, btnPrefH);
        addClass(acceptBtn, "loginBtn");
        return acceptBtn;
    }

    /**
     * Creates a choosing form with a label, two text fields and an accept button
     *
     * @return A Quintuple of BorderPane, AnchorPane, JFXTextField, JFXPasswordField, JFXButton.
     */
    private Quintuple<BorderPane, VBox, JFXTextField, JFXTextField, JFXButton> createChoosingForm() {
        Label topLbl = new Label(topLblText);
        VBox lblBox = Nodes.setUpNewVBox(0, topLblPrefW * 2, topLblPrefH * 2.5, Pos.CENTER, true, topLbl);
        topLbl.setTextOverrun(OverrunStyle.ELLIPSIS);
        topLbl.setWrapText(true);
        topLbl.setTextAlignment(TextAlignment.CENTER);
        topLbl.setMinHeight(topLblPrefH);

        addClass(topLbl, "loginTopLbl");

        JFXTextField usernameField = new JFXTextField();
        usernameField.setPromptText(usrPromptTxt);
        Nodes.setLayoutAndPrefSize(usernameField, usrLayoutX, usrLayoutY, usrPrefW, usrPrefH);
        addClass(usernameField, "loginField");

        JFXTextField pwdField = new JFXTextField();
        pwdField.setPromptText(pwdPromptTxt);
        Nodes.setLayoutAndPrefSize(pwdField, pwdLayoutX, pwdLayoutY, pwdPrefW, pwdPrefH);
        styleDefaultField(usernameField, focusColor, unfocusColor);
        styleDefaultField(pwdField, focusColor, unfocusColor);
        addClass(pwdField, "loginField");

        JFXButton acceptBtn = setupAcceptBtn(btnLoginText);
        String signupLblText = "See list of abbreviation & select Study Plan";
        this.abbrevBtn = createAbbrevBtn(signupLblText);
        abbrevBtn.setOnMouseClicked(me -> signupScreen());

        VBox loginFieldsCtnr = Nodes.setUpNewVBox(10, Pos.TOP_CENTER, false, lblBox, usernameField, pwdField);
        BorderPane bp = new BorderPane(loginFieldsCtnr);
        VBox bottom = Nodes.setUpNewVBox(1, Pos.CENTER, true, abbrevBtn, acceptBtn);

        bp.setBottom(bottom);
        BorderPane.setAlignment(bp.getBottom(), Pos.CENTER);
        acceptBtn.prefWidthProperty().bind(bp.widthProperty());

        loginFieldsCtnr.minWidthProperty().bind(this.center().widthProperty().multiply(0.99));

        loginFieldsCtnr.spacingProperty().bind(bp.heightProperty().multiply(0.14));
        // addClass(loginFieldsCtnr, "boxR");

        abbrevBtn.prefWidthProperty().bind(bp.widthProperty());

        lblBox.maxWidthProperty().bind(bp.widthProperty().multiply(0.9));
        lblBox.minWidthProperty().bind(bp.widthProperty().multiply(0.7));

        lblBox.maxHeightProperty().bind(bp.heightProperty().multiply(0.4));
        lblBox.minHeightProperty().bind(bp.heightProperty().multiply(0.25));
        VBox.setVgrow(loginFieldsCtnr, Priority.SOMETIMES);
        VBox.setVgrow(topLbl, Priority.ALWAYS);
        VBox.setVgrow(bottom, Priority.SOMETIMES);
        bp.heightProperty().addListener(
            (observable, oldValue, newValue) -> lblBox.setPadding(new Insets(newValue.doubleValue() * 0.05, 0, 0, 0)));

        return new Quintuple<>(bp, loginFieldsCtnr, usernameField, pwdField, acceptBtn);
    }

    /**
     * Creates mail field for sign up menu but does not register it to a parent node yet. It creates
     * a JFXTextField, sets its prompt text, styles it, adds a class to it, and sets its layout and
     * preferred size
     *
     * @return JFXTextField mail field.
     */
    private JFXTextField createMailField() {
        JFXTextField mailField = new JFXTextField();
        mailField.setPromptText("Email");
        styleDefaultField(mailField, WHITE, WHITE);
        addClass(mailField, "loginField");
        Nodes.setLayoutAndPrefSize(mailField, pwdLayoutX, mailLayoutY, pwdPrefW, pwdPrefH);
        mailField.prefWidthProperty().bind(studyPlanField.widthProperty());
        return mailField;
    }

    /**
     * Generates sign up button Creates a button with a label and adds a border to it when the mouse
     * enters the button.
     *
     * @return A JFXButton object.
     */
    static JFXButton createAbbrevBtn(String text) {
        double signupLabelPrefH = 45;
        String class1 = "signupLabel", classBorder = "signupLabelBorder";
        JFXButton abbrevBtn = new JFXButton(text);
        addClass(abbrevBtn, class1);
        abbrevBtn.setPrefHeight(signupLabelPrefH);

        abbrevBtn.setOnMouseEntered(me -> addClass(abbrevBtn, classBorder));
        abbrevBtn.setOnMouseClicked(me -> abbrevBtn.getStyleClass().remove(classBorder));
        abbrevBtn.setOnMousePressed(me -> abbrevBtn.getStyleClass().remove(classBorder));
        abbrevBtn.setOnMouseReleased(me -> abbrevBtn.getStyleClass().remove(classBorder));
        abbrevBtn.setOnMouseExited(me -> abbrevBtn.getStyleClass().remove(classBorder));
        return abbrevBtn;
    }

    private void styleDefaultField(JFXTextField field, Paint focusColor, Paint unfocusColor) {
        field.setFocusColor(focusColor);
        field.setUnFocusColor(unfocusColor);
        field.setLabelFloat(true);
    }

    private void styleDefaultField(JFXPasswordField field, Paint focusColor, Paint unfocusColor) {
        field.setFocusColor(focusColor);
        field.setUnFocusColor(unfocusColor);
        field.setLabelFloat(true);
    }

    /*
    void addToStudyPlanSelection(String spAbbrev) {
        if (!spSelectionViewValue.getValue().contains(spAbbrev)) {
            String previous = spSelectionViewValue.getValueSafe();
            String sep = previous.substring(spSelectionPropLabel.length()).isBlank() ? "" : ",";
            spSelectionViewValue.setValue(previous + sep + spAbbrev);
        }
    }
*/

    /**
     * Returns the bottom of the loginBp.
     *
     * @return The bottom of the BorderPane.
     */
    //private VBox loginBottom() { return (VBox) loginBp.getBottom(); }

    /*
     * ======================================================= ******************* GETTERS *************************** =======================================================
     */
    //    public BorderPane loginBp() { return this.loginBp; }
    public JFXTextField usernameField() { return this.courseField; }

    public JFXTextField pwdField() { return this.studyPlanField; }

    public JFXButton loginBtn() { return this.generateBtn; }
}
