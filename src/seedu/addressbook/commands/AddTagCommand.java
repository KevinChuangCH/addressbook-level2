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
 * Adds tags to a person  using it's last displayed index from the address book.
 */
public class AddTagCommand extends Command {

    public static final String COMMAND_WORD = "addtag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds one or more tags to the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX, TAG...\n"
            + "Example: " + COMMAND_WORD + " 1, friends";

    public static final String MESSAGE_ADD_TAG_SUCCESS = "Add tag to: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    private final UniqueTagList tagToAdd;

    /**
     * Convenience constructor using given input arguments.
     *
     * @throws IllegalValueException if any of the input arguments are invalid
     */
    public AddTagCommand(int targetVisibleIndex, Set<String> tags) throws IllegalValueException {
        super(targetVisibleIndex);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        tagToAdd = new UniqueTagList(tagSet);
    }

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = getTargetPerson();
            UniqueTagList updatedTagList = target.getTags();
            updatedTagList.mergeFrom(tagToAdd);
            final Person updatedPerson = new Person(
                    target.getName(),
                    target.getPhone(),
                    target.getEmail(),
                    target.getAddress(),
                    updatedTagList
            );
            addressBook.removePerson(target);
            addressBook.addPerson(updatedPerson);
            return new CommandResult(String.format(MESSAGE_ADD_TAG_SUCCESS, updatedPerson));

        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        } catch (UniquePersonList.DuplicatePersonException dpe) {
            return new CommandResult(MESSAGE_DUPLICATE_PERSON);
        }
    }

}
