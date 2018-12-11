package kate.cinema.dto;

import kate.cinema.entity.Schedule;

import java.util.List;

public class ScheduleListDTO {
    private List<Schedule> scheduleList;

    public ScheduleListDTO() {
    }

    public ScheduleListDTO(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @Override
    public String toString() {
        return "ScheduleListDTO{" +
                "scheduleList=" + scheduleList +
                '}';
    }
}
