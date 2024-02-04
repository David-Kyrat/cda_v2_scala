/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cda.view.jfxuserform;

import static cda.view.helpers.Nodes.addClass;

import cda.view.helpers.Nodes;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Wrapper around `com.jfoenix.control.JFXTreeTableView`.
 * Use `.get()` method to extract the underlying treeTableView
 * to add `this` to other nodes.
 */
public class JFXTableView {

    private final List<String> columnNames; //
    private final ObservableList<VPair> abbrevPairList;
    private final List<JFXTreeTableColumn<VPair, String>> tableColumns;

    // container of the tableView
    private final VBox parentRoot;
    private final JFXTreeTableView<VPair> tableView;
    private final Label label;
    private final MFXTextField filterField;
    private final static AtomicBoolean isCleanRegistered = new AtomicBoolean(false);

    private Label createLabel() {
        String labelTxt = "Double click to add a study-plan to selection.\n Enter a name in the search box below to filter the results.";
        Label lbl = new Label(labelTxt);
        lbl.setTextOverrun(OverrunStyle.ELLIPSIS);
        lbl.setWrapText(true);
        lbl.setTextAlignment(TextAlignment.CENTER);
        lbl.setMinHeight(50);
        lbl.getStylesheets().add("jfxuserform/login.css");
        lbl.setStyle("-fx-text-fill: black; -fx-font-size: 18px");
        addClass(lbl, "loginTopLbl");
        return lbl;
    }

    private void styleField(MFXTextField field) {
        addClass(field, "filterField");
        field.setPadding(new Insets(0, 0, 0, 5));
        field.setFloatMode(FloatMode.BORDER);
    }

    /**
     * Jfoenix TreeTableView Wrapper for view the content of the abbreviation file
     * 
     * @param abbrevFileContent content of file
     * @param columnNames       name of columns
     */
    public JFXTableView(
            List<String[]> abbrevFileContent,
            String... columnNames) {
        this.columnNames = Arrays.stream(columnNames).toList();
        this.abbrevPairList = FXCollections.observableArrayList(buildVPairs(abbrevFileContent));

        this.tableColumns = new ArrayList<>(this.columnNames.stream()
                .map(name -> new JFXTreeTableColumn<VPair, String>(name))
                .toList());
        this.tableColumns.forEach(tc -> tc.getStyleClass().addAll("table-view-column"));

        AtomicInteger i = new AtomicInteger();
        tableColumns.forEach(tc -> {
            tc.setPrefWidth(100);
            tc.setCellValueFactory(
                    (TreeTableColumn.CellDataFeatures<VPair, String> param) -> {
                        if (tc.validateValue(param)) {
                            return (i.getAndIncrement() % 2 == 0)
                                    ? param.getValue().getValue().name
                                    : param.getValue().getValue().id;
                        } else {
                            return tc.getComputedValue(param);
                        }
                    });
        });

        TreeItem<VPair> rootItem = new RecursiveTreeItem<>(
                abbrevPairList,
                RecursiveTreeObject::getChildren);
        this.tableView = new JFXTreeTableView<>(rootItem);
        tableView.getStyleClass().addAll("tree-table-view");
        tableView.setShowRoot(false);
        tableView.setEditable(false);
        tableView.getColumns().setAll(tableColumns);
        tableView.setColumnResizePolicy(
                TreeTableView.CONSTRAINED_RESIZE_POLICY);
        // tableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        this.parentRoot = Nodes.setUpNewVBox(
                5,
                Pos.TOP_CENTER,
                true,
                createLabel(),
                tableView);

        this.filterField = new MFXTextField();
        styleField(filterField);
        filterField
                .minWidthProperty()
                .bind(tableView.widthProperty().multiply(0.8));

        parentRoot.getChildren().add(filterField);
        this.label = new Label();
        label.getStyleClass().add("count-label");

        // final VPair pair = pairProp.getValue();
        filterField
                .textProperty()
                .addListener((o, oldVal, newVal) -> tableView.setPredicate(pairProperty -> pairProperty
                        .getValue().nameIgnoreCase.contains(newVal.toLowerCase())));
        parentRoot.getChildren().add(label);
        label
                .textProperty()
                .bind(
                        Bindings.createStringBinding(
                                () -> tableView.getCurrentItemsCount() + " results.",
                                tableView.currentItemsCountProperty()));
        tableView.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(tableView, Priority.ALWAYS);
        VBox.setVgrow(filterField, Priority.NEVER);
        VBox.setVgrow(label, Priority.NEVER);
        parentRoot.setPadding(new Insets(0, 0, 20, 0));

        if (!isCleanRegistered.get()) {
            isCleanRegistered.set(true);
            /* Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("JFXTableView: ShutdownHook");
                Platform.runLater(() -> {
                    System.out.println("in app thread");
                    cleanUp();
                });
            })); */
        }
            
    }

    /**
     * @return Selected abbreviation in the table (i.e. study plan abbreviation of
     *         the last row to have been clicked)
     */
    String getSelectedAbbreviation() {
        var selectedItem = tableView.getSelectionModel().getSelectedItem();
        return selectedItem == null ? "" : selectedItem.getValue().idClean;
    }

    /**
     * Sets the value of the `onMouseClicked` property.
     *
     * @param value Event handler to trigger when user clicks on this table
     */
    void setOnMouseClicked(EventHandler<? super MouseEvent> value) {
        tableView.setOnMouseClicked(value);
    }

    /**
     * @return Parent-Node of Underlying `JFXTreeTableView` to be able to add it to
     *         other nodes
     */
    public VBox get() {
        return parentRoot;
    }

    public MFXTextField filterField() {
        return filterField;
    }
    /**
     * Build the list of VPairs i.e. elements of table from the given list of
     * pairs (array of length 2)
     * 
     * @param fileContent content of file
     * @return list of VPairs i.e. elements of this tableView
     */
    private List<VPair> buildVPairs(List<String[]> fileContent) {
        return fileContent
                .parallelStream()
                .map(arr -> new VPair(arr[0], arr[1]))
                .toList();
    }

    private static final class VPair extends RecursiveTreeObject<VPair> {
        
        
        final SimpleStringProperty name;
        final SimpleStringProperty id;
        private final String nameIgnoreCase;
        private final String idClean; // doesnt contains space/tabs and is a fixed value

        VPair(String name, String id) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty("\t\t" + id);
            this.nameIgnoreCase = name.toLowerCase();
            this.idClean = id.strip();
        }
    }

    /**
     * When we interact with the textfield to filter results, 
     * => application can't exit normally anymore. ctrl-c doesn't work, 
     * close btn doesn't work it just make the app freeze.
     * Hence trying to resolve it here by freing stuff, clearing the field...
     */
    public void cleanUp() {
        // System.err.println("JFXTableView: in cleanUp");
        filterField.clear();
        abbrevPairList.clear();
        this.tableColumns.clear();
        parentRoot.getChildren().clear();
        /* this.filterField = null;
        this.parentRoot = null; */
        // System.err.println("JFXTableView: in cleanUp end");
    }
}
