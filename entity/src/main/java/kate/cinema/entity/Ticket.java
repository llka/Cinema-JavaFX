package kate.cinema.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

public class Ticket implements DatabaseEntity {
    private int id;
    @Positive
    private int placeNumber;
    private BigDecimal cost;
    @NotNull
    private Schedule schedule;

    public Ticket() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(int placeNumber) {
        this.placeNumber = placeNumber;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id == ticket.id &&
                placeNumber == ticket.placeNumber &&
                Objects.equals(cost, ticket.cost) &&
                Objects.equals(schedule, ticket.schedule);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, placeNumber, cost, schedule);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", placeNumber=" + placeNumber +
                ", cost=" + cost +
                ", schedule=" + schedule +
                '}';
    }
}
