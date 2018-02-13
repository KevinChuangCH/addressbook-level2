package seedu.addressbook.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.tag.Tag;

/**
 * Finds and lists all persons in address book whose tags contains any of the argument keywords.
 * Keyword matching is case sensitive.
 *
 * @throws IllegalValueException if any of the raw values are invalid
 */
public class FindTagCommand extends Command {

    public static final String COMMAND_WORD = "findtag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose tags contains "
            + "any of the specified keywords (case-sensitive) and displays them as a list with index numbers.\n"
            + "Parameter: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " friends owesMoney";

    private final Set<String> keywords;
    private final Set<Tag> keyTags;

    public FindTagCommand(Set<String> keywords) throws IllegalValueException{
        this.keywords = keywords;
        final Set<Tag> tagSet = new HashSet<>();
        for (String keyword : keywords) {
            tagSet.add(new Tag(keyword));
        }
        keyTags = tagSet;
    }

    /**
     * Returns a copy of keywords in this command.
     */
    public Set<String> getKeywords() {
        return new HashSet<>(keywords);
    }

    @Override
    public CommandResult execute() {
        final List<ReadOnlyPerson> personsFound = getPersonsWithTagContainingAnyKeyword(keyTags);
        return new CommandResult(getMessageForPersonListShownSummary(personsFound), personsFound);
    }

    /**
     * Retrieves all persons in the address book whose tags contain some of the specified keywords.
     *
     * @param keyTags for searching
     * @return list of persons found
     */
    private List<ReadOnlyPerson> getPersonsWithTagContainingAnyKeyword(Set<Tag> keyTags) {
        final List<ReadOnlyPerson> matchedPersons = new ArrayList<>();
        for (ReadOnlyPerson person : addressBook.getAllPersons()) {
            final Set<Tag> tagsOfPerson = person.getTags().toSet();
            if (!Collections.disjoint(tagsOfPerson, keyTags)) {
                matchedPersons.add(person);
            }
        }
        return matchedPersons;
    }
}
