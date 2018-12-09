package kate.cinema.backend.command;


import kate.cinema.backend.command.admin.CreateContactCommand;
import kate.cinema.backend.command.admin.DeleteContactCommand;
import kate.cinema.backend.command.guest.LogInCommand;
import kate.cinema.backend.command.guest.RegisterCommand;
import kate.cinema.backend.command.user.GetContactCommand;
import kate.cinema.backend.command.user.GetContactsCommand;
import kate.cinema.backend.command.user.LogOutCommand;
import kate.cinema.backend.command.user.UpdateContactCommand;
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
