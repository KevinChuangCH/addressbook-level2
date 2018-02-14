package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList;

import java.util.HashSet;
import java.util.Set;

/**
 * Updates a person tag using it's last displayed index from the address book.
 */
public class UpdateTagCommand extends Command {

    public static final String COMMAND_WORD = "updatetag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Updates the tag of the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX, TAG\n"
            + "Example: " + COMMAND_WORD + " 1, friends";

    public static final String MESSAGE_UPDATE_TAG_SUCCESS = "Updated Person: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    private final UniqueTagList tagToUpdate;

    public UpdateTagCommand(int targetVisibleIndex, Set<String> tags) throws IllegalValueException {
        super(targetVisibleIndex);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        tagToUpdate = new UniqueTagList(tagSet);
    }

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = getTargetPerson();
            UniqueTagList updatedTagList = target.getTags();
            updatedTagList.mergeFrom(tagToUpdate);
            final Person updatedPerson = new Person(
                    target.getName(),
                    target.getPhone(),
                    target.getEmail(),
                    target.getAddress(),
                    updatedTagList
            );
            addressBook.removePerson(target);
            addressBook.addPerson(updatedPerson);
            return new CommandResult(String.format(MESSAGE_UPDATE_TAG_SUCCESS, updatedPerson));

        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        } catch (UniquePersonList.DuplicatePersonException dpe) {
            return new CommandResult(MESSAGE_DUPLICATE_PERSON);
        }
    }

}
