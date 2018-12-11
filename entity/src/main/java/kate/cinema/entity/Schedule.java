package kate.cinema.entity;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

public class Schedule implements DatabaseEntity {
    private int id;
    @NotNull
    private Film film;
    @NotNull
    private Date date;

    public Schedule() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return id == schedule.id &&
                Objects.equals(film, schedule.film) &&
                Objects.equals(date, schedule.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, film, date);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", film=" + film +
                ", date=" + date +
                '}';
    }
}
