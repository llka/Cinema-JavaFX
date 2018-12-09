package kate.cinema;

import kate.cinema.entity.Contact;

import java.util.List;

public class ContactListDTO {
    private List<Contact> contacts;

    public ContactListDTO() {
    }

    public ContactListDTO(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "ContactListDTO{" +
                "contacts=" + contacts +
                '}';
    }
}
