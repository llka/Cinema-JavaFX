package kate.cinema.backend.command;


import kate.cinema.backend.command.admin.*;
import kate.cinema.backend.command.guest.LogInCommand;
import kate.cinema.backend.command.guest.RegisterCommand;
import kate.cinema.backend.command.user.*;
import kate.cinema.entity.enums.RoleEnum;

import java.util.EnumSet;
import java.util.Set;

public enum CommandType {
    LOGIN {
        {
            this.command = new LogInCommand();
            this.role = EnumSet.of(RoleEnum.GUEST);
        }
    },
    REGISTER {
        {
            this.command = new RegisterCommand();
            this.role = EnumSet.of(RoleEnum.GUEST);
        }
    },
    LOGOUT {
        {
            this.command = new LogOutCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },


    GET_CONTACT {
        {
            this.command = new GetContactCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },
    GET_CONTACTS {
        {
            this.command = new GetContactsCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },
    UPDATE_CONTACT {
        {
            this.command = new UpdateContactCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },
    CREATE_CONTACT {
        {
            this.command = new CreateContactCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    DELETE_CONTACT {
        {
            this.command = new DeleteContactCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },

    GET_FILMS {
        {
            this.command = new GetFilmsCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },

    BUY_TICKET {
        {
            this.command = new BuyTicketCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },

    DELETE_SCHEDULE {
        {
            this.command = new DeleteScheduleCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    UPDATE_SCHEDULE {
        {
            this.command = new UpdateScheduleCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    UPDATE_FILM {
        {
            this.command = new UpdateFilmCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    CREATE_FILM {
        {
            this.command = new CreateFilmCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },


    EMPTY {
        {
            this.command = new EmptyCommand();
            this.role = EnumSet.of(RoleEnum.GUEST, RoleEnum.USER, RoleEnum.ADMIN);
        }
    };

    public ActionCommand command;
    public Set<RoleEnum> role;

    public ActionCommand getCommand() {
        return command;
    }
}
