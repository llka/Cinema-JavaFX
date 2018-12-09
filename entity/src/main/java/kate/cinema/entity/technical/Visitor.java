package kate.cinema.entity.technical;

import kate.cinema.entity.Contact;
import kate.cinema.entity.enums.RoleEnum;

import java.util.Objects;

public class Visitor {
    private RoleEnum role;
    private Contact contact;

    public Visitor() {
        role = RoleEnum.GUEST;
    }

    public Visitor(RoleEnum role, Contact contact) {
        this.role = role;
        this.contact = contact;
    }

    public Visitor(RoleEnum role) {
        this.role = role;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visitor visitor = (Visitor) o;
        return role == visitor.role &&
                Objects.equals(contact, visitor.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, contact);
    }

    @Override
    public String toString() {
        return "Visitor{" +
                "role=" + role +
                ", contact=" + contact +
                '}';
    }
}
