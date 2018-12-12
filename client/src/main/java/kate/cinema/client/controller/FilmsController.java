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
import kate.cinema.dto.ScheduleListDTO;
import kate.cinema.entity.Schedule;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static kate.cinema.client.util.AlertUtil.alert;

public class FilmsController {
    private static final Logger logger = LogManager.getLogger(FilmsController.class);

    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String FILTER_PARAM_SCHEDULE_ID = "scheduleId";
    private static final String FILTER_PARAM_TITLE = "title";

    private static boolean firstOpened = true;


    @FXML
    private TableView table;
    @FXML
    private TableColumn<Schedule, Integer> ticketsLeftColumn;
    @FXML
    private TableColumn<Schedule, String> costColumn;
    @FXML
    private TableColumn<Schedule, String> titleColumn;
    @FXML
    private TableColumn<Schedule, String> dateColumn;
    @FXML
    private TableColumn<Schedule, Integer> durationColumn;
    @FXML
    private TableColumn<Schedule, Integer> idColumn;


    @FXML
    private Button searchBtn;
    @FXML
    private TextField searchByIdTextField;
    @FXML
    private Button getAllBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private TextField searchByTitleTextField;
    @FXML
    private TextField placeTextField;
    @FXML
    private Button buyTicketBtn;

    @FXML
    private void initialize() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        logger.debug("initialize");


        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilm().getTitle()));
        costColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilm().getTicketCost().toString()));
        ticketsLeftColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getFilm().getTicketsLeft()).asObject());
        durationColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getFilm().getDurationInMin()).asObject());

        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getDate()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        if (firstOpened) {
            fillTable();
            firstOpened = false;
        }
    }

    private void fillTable() {
        try {
            ContextHolder.getClient().sendRequest(new CommandRequest("GET_FILMS"));
            CommandResponse response = ContextHolder.getLastResponse();
            logger.debug("Response " + response);
            if (response.getStatus().is2xxSuccessful()) {
                ScheduleListDTO scheduleListDTO = JsonUtil.deserialize(response.getBody(), ScheduleListDTO.class);
                table.setItems(FXCollections.observableArrayList(scheduleListDTO.getScheduleList()));
            } else {
                alert(Alert.AlertType.ERROR, "Cannot fill in table!", response.getBody());
            }
        } catch (ClientException e) {
            logger.error(e);
            alert(Alert.AlertType.ERROR, "Cannot fill in table!", e.getMessage());
        }
    }


    @FXML
    private void populate(Schedule schedule) {
        ObservableList<Schedule> scheduleObservableList = FXCollections.observableArrayList();
        scheduleObservableList.add(schedule);
        table.setItems(scheduleObservableList);
    }

    @FXML
    private void populate(ObservableList<Schedule> scheduleObservableList) {
        table.setItems(scheduleObservableList);
    }


    @FXML
    void search(ActionEvent event) {
        Map<String, String> params = prepareFilterParams();
        try {
            ContextHolder.getClient().sendRequest(new CommandRequest("GET_FILMS", params));
            CommandResponse response = ContextHolder.getLastResponse();
            logger.debug("Response " + response);
            if (response.getStatus().is2xxSuccessful()) {
                ScheduleListDTO scheduleListDTO = JsonUtil.deserialize(response.getBody(), ScheduleListDTO.class);
                table.setItems(FXCollections.observableArrayList(scheduleListDTO.getScheduleList()));
            } else {
                alert(Alert.AlertType.ERROR, "Cannot fill in table!", response.getBody());
            }
        } catch (ClientException e) {
            logger.error(e);
            alert(Alert.AlertType.ERROR, "Cannot fill in table!", e.getMessage());
        }
    }

    @FXML
    void buyTicket(ActionEvent event) {
        String placeText = placeTextField.getText();

        if (placeText != null && !placeText.isEmpty()) {
            try {
                int place = Integer.parseInt(placeText);
                Map<String, String> params = prepareFilterParams();
                params.put("place", placeText);
                try {
                    ContextHolder.getClient().sendRequest(new CommandRequest("BUY_TICKET", params));
                    CommandResponse response = ContextHolder.getLastResponse();
                    logger.debug("Response " + response);
                    if (response.getStatus().is2xxSuccessful()) {
                        ScheduleListDTO scheduleListDTO = JsonUtil.deserialize(response.getBody(), ScheduleListDTO.class);
                        table.setItems(FXCollections.observableArrayList(scheduleListDTO.getScheduleList()));
                        alert("Successfully bought the ticket!");
                    } else {
                        alert(Alert.AlertType.ERROR, "Cannot fill in table!", response.getBody());
                    }
                } catch (ClientException e) {
                    logger.error(e);
                    alert(Alert.AlertType.ERROR, "Cannot fill in table!", e.getMessage());
                }
            } catch (IllegalArgumentException e) {
                alert(Alert.AlertType.ERROR, "Invalid place value!", "Use integers to define place number!");
            }

        }
    }

    @FXML
    void getAllSticks(ActionEvent event) {
        fillTable();
    }

    @FXML
    void refresh(ActionEvent event) {
        fillTable();
    }

    private Map<String, String> prepareFilterParams() {
        String id = searchByIdTextField.getText();
        String title = searchByTitleTextField.getText();
        Map<String, String> params = new HashMap<>();
        if (id != null && !id.isEmpty()) {
            params.put(FILTER_PARAM_SCHEDULE_ID, id);
        }
        if (title != null && !title.isEmpty()) {
            params.put(FILTER_PARAM_TITLE, title);
        }
        return params;
    }

    public static boolean isFirstOpened() {
        return firstOpened;
    }

    public static void setFirstOpened(boolean firstOpened) {
        FilmsController.firstOpened = firstOpened;
    }
}
