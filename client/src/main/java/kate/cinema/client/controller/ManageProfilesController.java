package kate.cinema.client.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kate.cinema.client.client.ContextHolder;
import kate.cinema.client.exception.ClientException;
import kate.cinema.client.util.JsonUtil;
import kate.cinema.dto.ContactListDTO;
import kate.cinema.entity.Contact;
import kate.cinema.entity.enums.RoleEnum;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static kate.cinema.client.util.AlertUtil.alert;


public class ManageProfilesController {
    private static final Logger logger = LogManager.getLogger(ManageProfilesController.class);

    private static boolean firstOpened = true;

    @FXML
    private TableView contactsTable;
    @FXML
    private TableColumn<Contact, Integer> userIdColumn;
    @FXML
    private TableColumn<Contact, String> userFirstNameColumn;
    @FXML
    private TableColumn<Contact, String> userLastNameColumn;
    @FXML
    private TableColumn<Contact, String> userEmailColumn;
    @FXML
    private TableColumn<Contact, String> userRoleColumn;

    @FXML
    private TextField searchByIdTextField;
    @FXML
    private TextField searchByEmailTextField;
    @FXML
    private ComboBox<String> updateRoleComboBox;

    @FXML
    private Button addUserBtn;
    @FXML
    private Button searchBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button getAllUsersBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private TextField newLastNameTextField;
    @FXML
    private TextField newFirstNameTextField;
    @FXML
    private TextField newEmailTextField;
    @FXML
    private TextField newPasswordTextField;

    @FXML
    private void initialize() {
        logger.debug("initialize");
        updateRoleComboBox.setItems(FXCollections.observableArrayList(
                RoleEnum.ADMIN.toString(),
                RoleEnum.USER.toString(),
                RoleEnum.GUEST.toString()));

        userIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        userFirstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getFirstName())));
        userLastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        userEmailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        userRoleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().toString()));
        if (firstOpened) {
            fillContactsTable();
            firstOpened = false;
        }
    }

    private void fillContactsTable() {
        try {
            ContextHolder.getClient().sendRequest(new CommandRequest("GET_CONTACTS"));
            CommandResponse response = ContextHolder.getLastResponse();
            logger.debug("Response " + response);
            if (response.getStatus().is2xxSuccessful()) {
                ContactListDTO contactsDTO = JsonUtil.deserialize(response.getBody(), ContactListDTO.class);
                logger.debug(contactsDTO.getContacts());
                contactsTable.setItems(FXCollections.observableArrayList(contactsDTO.getContacts()));
            } else {
                alert(Alert.AlertType.ERROR, "Cannot fill in Contacts table!", response.getBody());
            }
        } catch (ClientException e) {
            logger.error(e);
            alert(Alert.AlertType.ERROR, "Cannot fill in Contacts table!", e.getMessage());
        }
    }

    @FXML
    private void populateContact(Contact contact) {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();
        contactList.add(contact);
        contactsTable.setItems(contactList);
    }

    @FXML
    private void populateContacts(ObservableList<Contact> contactObservableList) {
        contactsTable.setItems(contactObservableList);
    }


    @FXML
    void search(ActionEvent event) {
        String id = searchByIdTextField.getText();
        String email = searchByEmailTextField.getText();
        Map<String, String> params = new HashMap<>();
        if (id != null && !id.isEmpty()) {
            params.put("id", id);
        }
        if (email != null && !email.isEmpty()) {
            params.put("email", email);
        }
        if (email == null && id == null) {
            alert("Enter an email or id to search by!");
        } else {
            try {
                ContextHolder.getClient().sendRequest(new CommandRequest("GET_CONTACT", params));
                CommandResponse response = ContextHolder.getLastResponse();
                logger.debug("Response " + response);
                if (response.getStatus().is2xxSuccessful()) {
                    Contact contact = JsonUtil.deserialize(response.getBody(), Contact.class);
                    logger.debug(contact);
                    populateContact(contact);
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot find contact!", response.getBody());
                }
            } catch (ClientException e) {
                logger.error(e);
                alert(Alert.AlertType.ERROR, "Cannot find contact!", e.getMessage());
            }
        }
    }

    @FXML
    void delete(ActionEvent event) {
        String id = searchByIdTextField.getText();
        String email = searchByEmailTextField.getText();
        Map<String, String> params = new HashMap<>();
        if (id != null && !id.isEmpty()) {
            params.put("id", id);
        }
        if (email != null && !email.isEmpty()) {
            params.put("email", email);
        }
        if (email == null && id == null) {
            alert("Enter an email or id to search by!");
        } else {
            try {
                ContextHolder.getClient().sendRequest(new CommandRequest("DELETE_CONTACT", params));
                CommandResponse response = ContextHolder.getLastResponse();
                logger.debug("Response " + response);
                if (response.getStatus().is2xxSuccessful()) {
                    fillContactsTable();
                    alert("Contact was successfully deleted!");
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot find contact!", response.getBody());
                }
            } catch (ClientException e) {
                logger.error(e);
                alert(Alert.AlertType.ERROR, "Cannot find contact!", e.getMessage());
            }
        }
    }

    @FXML
    void update(ActionEvent event) {
        String updatedRole = updateRoleComboBox.getValue();
        String id = searchByIdTextField.getText();
        String email = searchByEmailTextField.getText();
        Map<String, String> params = new HashMap<>();

        if (updatedRole != null && !updatedRole.isEmpty()) {
            try {
                RoleEnum role = RoleEnum.valueOf(updatedRole);
                params.put("role", updatedRole);

                if (id != null && !id.isEmpty()) {
                    params.put("id", id);
                }
                if (email != null && !email.isEmpty()) {
                    params.put("email", email);
                }
                if (email == null && id == null) {
                    alert("Enter an email or id to search by!");
                } else {
                    try {
                        ContextHolder.getClient().sendRequest(new CommandRequest("GET_CONTACT", params));
                        CommandResponse response = ContextHolder.getLastResponse();
                        logger.debug("Response " + response);
                        if (response.getStatus().is2xxSuccessful()) {
                            Contact contact = JsonUtil.deserialize(response.getBody(), Contact.class);
                            contact.setRole(role);
                            ContextHolder.getClient().sendRequest(new CommandRequest("UPDATE_CONTACT", JsonUtil.serialize(contact)));
                            CommandResponse responseWithUpdatedContact = ContextHolder.getLastResponse();
                            if (responseWithUpdatedContact.getStatus().is2xxSuccessful()) {
                                logger.debug(contact);
                                populateContact(contact);
                            } else {
                                alert(Alert.AlertType.ERROR, "Cannot update contact!", responseWithUpdatedContact.getBody());
                            }
                        } else {
                            alert(Alert.AlertType.ERROR, "Cannot find contact!", response.getBody());
                        }
                    } catch (ClientException e) {
                        logger.error(e);
                        alert(Alert.AlertType.ERROR, "Cannot find contact!", e.getMessage());
                    }
                }
            } catch (IllegalArgumentException e) {
                alert("Invalid role! Use USER or ADMIN");
            }
        }
    }

    @FXML
    void addUser(ActionEvent event) {
        String firstName = newFirstNameTextField.getText();
        String lastName = newLastNameTextField.getText();
        String email = newEmailTextField.getText();
        String password = newPasswordTextField.getText();

        if (firstName != null && !firstName.isEmpty()
                && lastName != null && !lastName.isEmpty()
                && email != null && !email.isEmpty()
                && password != null && !password.isEmpty()) {

            Contact contact = new Contact();
            contact.setFirstName(firstName);
            contact.setLastName(lastName);
            contact.setPassword(password);
            contact.setEmail(email);
            contact.setRole(RoleEnum.USER);
            try {
                ContextHolder.getClient().sendRequest(new CommandRequest("CREATE_CONTACT", JsonUtil.serialize(contact)));
                CommandResponse response = ContextHolder.getLastResponse();
                logger.debug("Response " + response);
                if (response.getStatus().is2xxSuccessful()) {
                    fillContactsTable();
                    alert("Contact was successfully added!");
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot add contact!", response.getBody());
                }
            } catch (ClientException e) {
                logger.error(e);
                alert(Alert.AlertType.ERROR, "Cannot add contact!", e.getMessage());
            }

        } else {
            alert(Alert.AlertType.WARNING, "Invalid data for new Contact!", "Invalid data for new Contact!");
        }
    }

    @FXML
    void getAllUsers(ActionEvent event) {
        fillContactsTable();
    }


    public static boolean isFirstOpened() {
        return firstOpened;
    }

    public static void setFirstOpened(boolean firstOpened) {
        ManageProfilesController.firstOpened = firstOpened;
    }
}
