package kate.cinema.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import kate.cinema.client.client.ContextHolder;
import kate.cinema.client.exception.ClientException;
import kate.cinema.client.util.JsonUtil;
import kate.cinema.entity.Contact;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import static kate.cinema.client.util.AlertUtil.alert;

public class MyProfileController {
    private static final Logger logger = LogManager.getLogger(MyProfileController.class);

    private static boolean first = true;

    @FXML
    private AnchorPane myProfilePane;

    @FXML
    private Button saveChangesBtn;

    @FXML
    private TextField myFirstNameTextField;

    @FXML
    private TextField myLastNameTextField;

    @FXML
    private TextField myEmailTextField;

    @FXML
    private TextField myPasswordTextField;

    @FXML
    void saveChanges(ActionEvent event) {
        Contact contact = ContextHolder.getSession().getVisitor().getContact();
        contact.setFirstName(myFirstNameTextField.getText());
        contact.setLastName(myLastNameTextField.getText());
        contact.setPassword(myPasswordTextField.getText());

        try {
            ContextHolder.getClient().sendRequest(new CommandRequest("UPDATE_CONTACT", JsonUtil.serialize(contact)));
            logger.debug("Request sent " + contact);


            CommandResponse response = ContextHolder.getLastResponse();
            logger.debug("Response " + response);
            if (response.getStatus().is2xxSuccessful()) {
                Contact updatedContact = JsonUtil.deserialize(response.getBody(), Contact.class);
                ContextHolder.getSession().getVisitor().setContact(updatedContact);
                ContextHolder.getSession().getVisitor().setRole(updatedContact.getRole());
                logger.debug("session " + ContextHolder.getSession());

                myFirstNameTextField.setText(updatedContact.getFirstName());
                myLastNameTextField.setText(updatedContact.getLastName());
                myEmailTextField.setText(updatedContact.getEmail());
                myPasswordTextField.setText(updatedContact.getPassword());

                alert("Successfully saved changes!");
            } else {
                alert(Alert.AlertType.ERROR, "Cannot logout!", response.getBody());
            }

        } catch (ClientException e) {
            alert(Alert.AlertType.ERROR, "Cannot save changes!", e.getMessage());
        }


    }

    @FXML
    void onMouseEntered(MouseEvent event) {
        if (first) {
            Contact contact = ContextHolder.getSession().getVisitor().getContact();

            myFirstNameTextField.setText(contact.getFirstName());
            myLastNameTextField.setText(contact.getLastName());
            myEmailTextField.setText(contact.getEmail());
            myPasswordTextField.setText(contact.getPassword());

            myFirstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveChangesBtn.setDisable(newValue.trim().isEmpty());
            });
            myLastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveChangesBtn.setDisable(newValue.trim().isEmpty());
            });
            myPasswordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveChangesBtn.setDisable(newValue.trim().isEmpty());
            });
            first = false;
        }
    }

    public static boolean isFirst() {
        return first;
    }

    public static void setFirst(boolean uploadContactInfo) {
        first = uploadContactInfo;
    }
}

