package kate.cinema.client.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kate.cinema.client.client.ContextHolder;
import kate.cinema.client.exception.ClientException;
import kate.cinema.client.util.JsonUtil;
import kate.cinema.entity.Contact;
import kate.cinema.entity.Ticket;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static kate.cinema.client.util.AlertUtil.alert;

public class MyTicketsController {
    private static final Logger logger = LogManager.getLogger(MyProfileController.class);
    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static boolean first = true;

    @FXML
    private Button refreshBtn;

    @FXML
    private TableView table;
    @FXML
    private TableColumn<Ticket, String> costColumn;
    @FXML
    private TableColumn<Ticket, Integer> placeColumn;
    @FXML
    private TableColumn<Ticket, String> filmTitleColumn;
    @FXML
    private TableColumn<Ticket, String> dateColumn;
    @FXML
    private TableColumn<Ticket, Integer> idColumn;

    @FXML
    private void initialize() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        logger.debug("initialize");

        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        placeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPlaceNumber()).asObject());
        costColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCost().toString()));
        filmTitleColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSchedule() != null
                    && cellData.getValue().getSchedule().getFilm() != null
                    && cellData.getValue().getSchedule().getFilm().getTitle() != null) {
                return new SimpleStringProperty(cellData.getValue().getSchedule().getFilm().getTitle());
            } else {
                return new SimpleStringProperty("");
            }
        });
        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSchedule() != null
                    && cellData.getValue().getSchedule().getDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getSchedule().getDate()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        if (first) {
            fillTable();
            first = false;
        }
    }

    private void fillTable() {
        refresh(new ActionEvent());
    }

    @FXML
    void refresh(ActionEvent event) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(ContextHolder.getSession().getVisitor().getContact().getId()));
            ContextHolder.getClient().sendRequest(new CommandRequest("GET_CONTACT", params));
            CommandResponse response = ContextHolder.getLastResponse();
            logger.debug("Response " + response);
            if (response.getStatus().is2xxSuccessful()) {
                Contact contact = JsonUtil.deserialize(response.getBody(), Contact.class);
                ContextHolder.getSession().getVisitor().setContact(contact);
                table.setItems(FXCollections.observableArrayList(contact.getTickets()));
            } else {
                alert(Alert.AlertType.ERROR, "Cannot refresh tickets table!", response.getBody());
            }
        } catch (ClientException e) {
            logger.error(e);
            alert(Alert.AlertType.ERROR, "Cannot refresh tickets table!", e.getMessage());
        }
    }


    public static boolean isFirst() {
        return first;
    }

    public static void setFirst(boolean uploadContactInfo) {
        first = uploadContactInfo;
    }
}
