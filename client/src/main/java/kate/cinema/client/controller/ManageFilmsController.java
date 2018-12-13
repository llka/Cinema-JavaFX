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
import kate.cinema.entity.Film;
import kate.cinema.entity.Schedule;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static kate.cinema.client.util.AlertUtil.alert;

public class ManageFilmsController {
    private static final Logger logger = LogManager.getLogger(ManageFilmsController.class);

    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String FILTER_PARAM_SCHEDULE_ID = "scheduleId";
    private static final String FILTER_PARAM_TITLE = "title";

    private static final String UPDATE_FILM_ID = "filmIdUpdate";
    private static final String UPDATE_PARAM_COST = "costToUpdate";
    private static final String UPDATE_PARAM_DURATION = "durationToUpdate";
    private static final String UPDATE_PARAM_TICKETS = "ticketsCountToUpdate";
    private static final String UPDATE_PARAM_DATE = "dateToUpdate";
    private static final String UPDATE_PARAM_TITLE = "titleToUpdate";


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
    private TextField searchByIdTextField;
    @FXML
    private TextField searchByTitleTextField;

    @FXML
    private Button getAllBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button searchBtn;


    @FXML
    private TextField costUpdate;
    @FXML
    private TextField durationUpdate;
    @FXML
    private TextField scheduleIdupdate;
    @FXML
    private TextField ticketsCountUpdate;
    @FXML
    private TextField filmIdUpdate;
    @FXML
    private DatePicker dateUpdate;
    @FXML
    private TextField titleUpdate;

    @FXML
    private Button updateByScheduleBtn;
    @FXML
    private Button updateByFilmIdBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button deleteBtn;


    @FXML
    private void initialize() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        logger.debug("initialize");


        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilm().getTitle()));
        costColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTicketCost().toString()));
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
    void getAll(ActionEvent event) {
        fillTable();
    }

    @FXML
    void refresh(ActionEvent event) {
        fillTable();
    }


    @FXML
    void delete(ActionEvent event) {
        Map<String, String> params = prepareFilterParams();
        try {
            ContextHolder.getClient().sendRequest(new CommandRequest("DELETE_SCHEDULE", params));
            CommandResponse response = ContextHolder.getLastResponse();
            if (response.getStatus().is2xxSuccessful()) {
                ScheduleListDTO scheduleListDTO = JsonUtil.deserialize(response.getBody(), ScheduleListDTO.class);
                table.setItems(FXCollections.observableArrayList(scheduleListDTO.getScheduleList()));
                alert("Successfully deleted!");
            } else {
                alert(Alert.AlertType.ERROR, "Cannot delete schedule!", response.getBody());
            }
        } catch (ClientException e) {
            logger.error(e);
            alert(Alert.AlertType.ERROR, "Cannot delete schedule!", e.getMessage());
        }
    }

    @FXML
    void updateByScheduleId(ActionEvent event) {
        if (scheduleIdupdate.getText() == null || scheduleIdupdate.getText().isEmpty()) {
            alert(Alert.AlertType.ERROR, "Cannot update Schedule!", "Invalid schedule Id!");
        } else {
            Map<String, String> params = prepareFilterParams();
            params.put("scheduleUpdate", scheduleIdupdate.getText().trim());
            try {
                ContextHolder.getClient().sendRequest(new CommandRequest("UPDATE_SCHEDULE", params));
                CommandResponse response = ContextHolder.getLastResponse();
                if (response.getStatus().is2xxSuccessful()) {
                    ScheduleListDTO scheduleListDTO = JsonUtil.deserialize(response.getBody(), ScheduleListDTO.class);
                    table.setItems(FXCollections.observableArrayList(scheduleListDTO.getScheduleList()));
                    alert("Successfully updated!");
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot update schedule!", response.getBody());
                }
            } catch (ClientException e) {
                logger.error(e);
                alert(Alert.AlertType.ERROR, "Cannot fill in table!", e.getMessage());
            }
        }
    }

    @FXML
    void addFilm(ActionEvent event) {
        Map<String, String> params = prepareFilterParams();
        if (params == null || params.isEmpty()
                || params.get(UPDATE_PARAM_TITLE) == null || params.get(UPDATE_PARAM_TITLE).isEmpty()
                || params.get(UPDATE_PARAM_TICKETS) == null || params.get(UPDATE_PARAM_TICKETS).isEmpty()
                || params.get(UPDATE_PARAM_DURATION) == null || params.get(UPDATE_PARAM_DURATION).isEmpty()) {
            alert(Alert.AlertType.ERROR, "Cannot create film!", "Not enough info!");
        } else {
            Film film = new Film();
            film.setTitle(params.get(UPDATE_PARAM_TITLE));
            try {
                film.setTicketsLeft(Integer.parseInt(params.get(UPDATE_PARAM_TICKETS)));
                film.setDurationInMin(Integer.parseInt(params.get(UPDATE_PARAM_DURATION)));
            } catch (NumberFormatException e) {
                alert(Alert.AlertType.ERROR, "Cannot create film!", "Invalid data!");
                return;
            }

            try {
                ContextHolder.getClient().sendRequest(new CommandRequest("CREATE_FILM", JsonUtil.serialize(film), params));
                CommandResponse response = ContextHolder.getLastResponse();
                if (response.getStatus().is2xxSuccessful()) {
                    ScheduleListDTO scheduleListDTO = JsonUtil.deserialize(response.getBody(), ScheduleListDTO.class);
                    table.setItems(FXCollections.observableArrayList(scheduleListDTO.getScheduleList()));
                    alert("Successfully created a new film!");
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot create a new film!", response.getBody());
                }
            } catch (ClientException e) {
                logger.error(e);
                alert(Alert.AlertType.ERROR, "Cannot create a new film!", e.getMessage());
            }
        }
    }

    @FXML
    void updateByFilmId(ActionEvent event) {
        if (filmIdUpdate.getText() == null || filmIdUpdate.getText().isEmpty()) {
            alert(Alert.AlertType.ERROR, "Cannot update Film!", "Invalid film Id!");
        } else {
            Map<String, String> params = prepareFilterParams();
            params.put(UPDATE_FILM_ID, filmIdUpdate.getText().trim());
            try {
                ContextHolder.getClient().sendRequest(new CommandRequest("UPDATE_FILM", params));
                CommandResponse response = ContextHolder.getLastResponse();
                if (response.getStatus().is2xxSuccessful()) {
                    ScheduleListDTO scheduleListDTO = JsonUtil.deserialize(response.getBody(), ScheduleListDTO.class);
                    table.setItems(FXCollections.observableArrayList(scheduleListDTO.getScheduleList()));
                    alert("Successfully updated!");
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot update film!", response.getBody());
                }
            } catch (ClientException e) {
                logger.error(e);
                alert(Alert.AlertType.ERROR, "Cannot update film!", e.getMessage());
            }
        }
    }

    private Map<String, String> prepareFilterParams() {
        String id = searchByIdTextField.getText();
        String title = searchByTitleTextField.getText();

        String costToUpdate = costUpdate.getText();
        String durationToUpdate = durationUpdate.getText();
        String ticketsCountToUpdate = ticketsCountUpdate.getText();
        LocalDate dateToUpdate = dateUpdate.getValue();
        String titleToUpdate = titleUpdate.getText();


        Map<String, String> params = new HashMap<>();
        if (id != null && !id.isEmpty()) {
            params.put(FILTER_PARAM_SCHEDULE_ID, id);
        }
        if (title != null && !title.isEmpty()) {
            params.put(FILTER_PARAM_TITLE, title);
        }

        if (costToUpdate != null && !costToUpdate.isEmpty()) {
            params.put(UPDATE_PARAM_COST, costToUpdate);
        }
        if (durationToUpdate != null && !durationToUpdate.isEmpty()) {
            params.put(UPDATE_PARAM_DURATION, durationToUpdate);
        }
        if (ticketsCountToUpdate != null && !ticketsCountToUpdate.isEmpty()) {
            params.put(UPDATE_PARAM_TICKETS, ticketsCountToUpdate);
        }
        if (dateToUpdate != null) {
            Instant instant = Instant.from(dateToUpdate.atStartOfDay(ZoneId.systemDefault()));
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
            Date date = Date.from(instant);
            params.put(UPDATE_PARAM_DATE, dateFormat.format(date));
        }
        if (titleToUpdate != null && !titleToUpdate.isEmpty()) {
            params.put(UPDATE_PARAM_TITLE, titleToUpdate);
        }

        return params;
    }

    public static boolean isFirstOpened() {
        return firstOpened;
    }

    public static void setFirstOpened(boolean firstOpened) {
        ManageFilmsController.firstOpened = firstOpened;
    }
}
