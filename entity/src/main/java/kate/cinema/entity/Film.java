package kate.cinema.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

public class Film implements DatabaseEntity{
    private int id;
    @NotBlank
    private String title;
    @Positive
    private int durationInMin;
    private BigDecimal ticketCost;
    private int ticketsLeft;

    public Film() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDurationInMin() {
        return durationInMin;
    }

    public void setDurationInMin(int durationInMin) {
        this.durationInMin = durationInMin;
    }

    public BigDecimal getTicketCost() {
        return ticketCost;
    }

    public void setTicketCost(BigDecimal ticketCost) {
        this.ticketCost = ticketCost;
    }

    public int getTicketsLeft() {
        return ticketsLeft;
    }

    public void setTicketsLeft(int ticketsLeft) {
        this.ticketsLeft = ticketsLeft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id &&
                durationInMin == film.durationInMin &&
                ticketsLeft == film.ticketsLeft &&
                Objects.equals(title, film.title) &&
                Objects.equals(ticketCost, film.ticketCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, durationInMin, ticketCost, ticketsLeft);
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", durationInMin=" + durationInMin +
                ", ticketCost=" + ticketCost +
                ", ticketsLeft=" + ticketsLeft +
                '}';
    }
}
