package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;

/**
 * Updates a person tag using it's last displayed index from the address book.
 */
public class UpdateTagCommand extends Command {

    public static final String COMMAND_WORD = "updatetag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Updates the tag of the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX TAG\n"
            + "Example: " + COMMAND_WORD + " 1 friends";

    public static final String MESSAGE_UPDATE_TAG_SUCCESS = "Updated Person: %1$s";

    public UpdateTagCommand(int targetVisibleIndex) {
        super(targetVisibleIndex);
    }

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = getTargetPerson();
            addressBook.removePerson(target);
            return new CommandResult(String.format(MESSAGE_UPDATE_TAG_SUCCESS, target));

        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }
    }

}
