package com.studentms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;

public class Controller {

    @FXML private TextField            txtName;
    @FXML private TextField            txtCourse;
    @FXML private ChoiceBox<YearLevel> cbYear;

    @FXML private TableView<Student>            table;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String>  colName;
    @FXML private TableColumn<Student, String>  colCourse;
    @FXML private TableColumn<Student, String>  colYear;

    @FXML private Label lblStatus;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private Connection conn;
    private int selectedId = -1;

    @FXML
    public void initialize() {
        conn = DBConnection.connect();

        if (conn == null) {
            showAlert(AlertType.ERROR, "Connection Error", "Could not connect to the database.");
            return;
        }

        cbYear.getItems().setAll(YearLevel.values());

        colId.setCellValueFactory(    data -> data.getValue().idProperty().asObject());
        colName.setCellValueFactory(  data -> data.getValue().nameProperty());
        colCourse.setCellValueFactory(data -> data.getValue().courseProperty());
        colYear.setCellValueFactory(  data -> data.getValue().yearLevelProperty());

        loadData();

        table.setOnMouseClicked(event -> {
            Student selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedId = selected.getId();
                txtName.setText(selected.getName());
                txtCourse.setText(selected.getCourse());
                cbYear.setValue(YearLevel.fromString(selected.getYearLevel()));
                setStatus("Selected: " + selected.getName());
            }
        });
    }

    private void loadData() {
        studentList.clear();
        String query = "SELECT * FROM students ORDER BY id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(query)) {

            while (rs.next()) {
                studentList.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getString("year_level")
                ));
            }
            table.setItems(studentList);
            setStatus("Loaded " + studentList.size() + " record(s).");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Load Error", "Failed to load records: " + e.getMessage());
        }
    }

    @FXML
    private void addStudent() {
        if (!validateInputs()) return;

        String query = "INSERT INTO students(name, course, year_level) VALUES (?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, txtName.getText().trim());
            pst.setString(2, txtCourse.getText().trim());
            pst.setString(3, cbYear.getValue().toString());
            pst.executeUpdate();
            loadData();
            clearFields();
            setStatus("Student added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Insert Error", "Failed to add student: " + e.getMessage());
        }
    }

    @FXML
    private void updateStudent() {
        if (selectedId == -1) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a student from the table first.");
            return;
        }
        if (!validateInputs()) return;

        String query = "UPDATE students SET name=?, course=?, year_level=? WHERE id=?";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, txtName.getText().trim());
            pst.setString(2, txtCourse.getText().trim());
            pst.setString(3, cbYear.getValue().toString());
            pst.setInt(4, selectedId);
            pst.executeUpdate();
            loadData();
            clearFields();
            setStatus("Student updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Update Error", "Failed to update student: " + e.getMessage());
        }
    }

    @FXML
    private void deleteStudent() {
        if (selectedId == -1) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a student from the table first.");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION,
                "Are you sure you want to delete this student?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                String query = "DELETE FROM students WHERE id=?";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setInt(1, selectedId);
                    pst.executeUpdate();
                    loadData();
                    clearFields();
                    setStatus("Student deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Delete Error", "Failed to delete student: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void clearFields() {
        txtName.clear();
        txtCourse.clear();
        cbYear.setValue(null);
        selectedId = -1;
        table.getSelectionModel().clearSelection();
        setStatus("Fields cleared.");
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty() ||
            txtCourse.getText().trim().isEmpty() ||
            cbYear.getValue() == null) {
            showAlert(AlertType.WARNING, "Validation Error", "All fields are required.");
            return false;
        }
        return true;
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setStatus(String message) {
        if (lblStatus != null) lblStatus.setText(message);
    }
}
