package seedu.addressbook.commands;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList;
import seedu.addressbook.ui.TextUi;
import seedu.addressbook.util.TestUtil;

public class AddTagCommandTest {

    private AddressBook emptyAddressBook;
    private AddressBook addressBook;

    private List<ReadOnlyPerson> emptyDisplayList;
    private List<ReadOnlyPerson> listWithEveryone;
    private List<ReadOnlyPerson> listWithSurnameDoe;

    private final Set<String> singleTestTags = new HashSet<>();
    private final Set<String> multipleTestTags = new HashSet<>();

    @Before
    public void setUp() throws Exception {
        Person johnDoe = new Person(new Name("Calvin Tan"), new Phone("93844567", false),
                new Email("john@doe.com", false), new Address("395C Ben Road", false), new UniqueTagList());
        Person danDoe = new Person(new Name("Dan Doe"), new Phone("1234556", true), new Email("ss@tt.com", true),
                new Address("NUS", true), new UniqueTagList(new Tag("Test")));
        Person samDoe = new Person(new Name("Sam Doe"), new Phone("63345566", false),
                new Email("sam@doe.com", false), new Address("55G Abc Road", false), new UniqueTagList());
        Person davidGrant = new Person(new Name("David Grant"), new Phone("61121122", false),
                new Email("david@grant.com", false), new Address("44H Define Road", false),
                new UniqueTagList(new Tag("friend")));

        emptyAddressBook = TestUtil.createAddressBook();
        addressBook = TestUtil.createAddressBook(johnDoe, danDoe, davidGrant, samDoe);

        emptyDisplayList = TestUtil.createList();

        listWithEveryone = TestUtil.createList(johnDoe, danDoe, davidGrant, samDoe);
        listWithSurnameDoe = TestUtil.createList(johnDoe, danDoe, samDoe);

        singleTestTags.add("single");
        for (int i = 1; i < 6; i++) {
            multipleTestTags.add("testTag" + i);
        }
    }

    @Test
    public void execute_noPersonDisplayed_returnsInvalidIndexMessage() {
        assertAddFailsDueToInvalidIndex(1, singleTestTags, addressBook, emptyDisplayList);
    }

    @Test
    public void execute_invalidIndex_returnsInvalidIndexMessage() {
        assertAddFailsDueToInvalidIndex(0, singleTestTags, addressBook, listWithEveryone);
        assertAddFailsDueToInvalidIndex(-1, singleTestTags, addressBook, listWithEveryone);
        assertAddFailsDueToInvalidIndex(listWithEveryone.size() + 1, singleTestTags, addressBook, listWithEveryone);
    }

    @Test
    public void execute_validIndex_singleTagAdded() throws PersonNotFoundException {
        assertAddSuccessful(1, singleTestTags, addressBook, listWithSurnameDoe);
        assertAddSuccessful(listWithSurnameDoe.size(), singleTestTags, addressBook, listWithSurnameDoe);

        int middleIndex = (listWithSurnameDoe.size() / 2) + 1;
        assertAddSuccessful(middleIndex, singleTestTags ,addressBook, listWithSurnameDoe);
    }

    @Test
    public void execute_validIndex_multipleTagsAdded() throws PersonNotFoundException {
        assertAddSuccessful(1, multipleTestTags, addressBook, listWithSurnameDoe);
        assertAddSuccessful(listWithSurnameDoe.size(), multipleTestTags, addressBook, listWithSurnameDoe);

        int middleIndex = (listWithSurnameDoe.size() / 2) + 1;
        assertAddSuccessful(middleIndex, multipleTestTags ,addressBook, listWithSurnameDoe);
    }

    /**
     * Creates a new addtag command.
     *
     * @param targetVisibleIndex of the person that we want to add tag to
     */
    private Command createAddTagCommand(int targetVisibleIndex, Set<String> tagsToAdd,
                                                    AddressBook addressBook, List<ReadOnlyPerson> displayList) {
        try {
            AddTagCommand command = new AddTagCommand(targetVisibleIndex, tagsToAdd);
            command.setData(addressBook, displayList);

            return command;
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Executes the command, and checks that the execution was what we had expected.
     */
    private void assertCommandBehaviour(Command addTagCommand, String expectedMessage,
                                        AddressBook expectedAddressBook, AddressBook actualAddressBook) {

        CommandResult result = addTagCommand.execute();

        assertEquals(expectedMessage, result.feedbackToUser);
        assertEquals(expectedAddressBook.getAllPersons(), actualAddressBook.getAllPersons());
    }

    /**
     * Asserts that the index is not valid for the given display list.
     */
    private void assertAddFailsDueToInvalidIndex(int invalidVisibleIndex, Set<String> tagSet,
                                                      AddressBook addressBook, List<ReadOnlyPerson> displayList) {

        String expectedMessage = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

        Command command = createAddTagCommand(invalidVisibleIndex, tagSet, addressBook, displayList);
        assertCommandBehaviour(command, expectedMessage, addressBook, addressBook);
    }

    /**
     * Asserts that adding tags to the person at the specified index can be successfully.
     *
     * The addressBook passed in will not be modified (no side effects).
     *
     * @throws PersonNotFoundException if the selected person is not in the address book
     */
    private void assertAddSuccessful(int targetVisibleIndex, Set<String> tagSet, AddressBook addressBook,
                                          List<ReadOnlyPerson> displayList) throws PersonNotFoundException {

        ReadOnlyPerson targetPerson = displayList.get(targetVisibleIndex - TextUi.DISPLAYED_INDEX_OFFSET);

        try{
            Set<Tag> newTags = new HashSet<>();
            for (String tagName : tagSet) {
                newTags.add(new Tag(tagName));
            }
            UniqueTagList tagToAdd = new UniqueTagList(newTags);
            UniqueTagList updatedTagList = targetPerson.getTags();
            updatedTagList.mergeFrom(tagToAdd);
            final Person updatedPerson = new Person(
                    targetPerson.getName(),
                    targetPerson.getPhone(),
                    targetPerson.getEmail(),
                    targetPerson.getAddress(),
                    updatedTagList
            );

            AddressBook expectedAddressBook = TestUtil.clone(addressBook);
            expectedAddressBook.removePerson(targetPerson);
            expectedAddressBook.addPerson(updatedPerson);
            String expectedMessage = String.format(AddTagCommand.MESSAGE_ADD_TAG_SUCCESS, updatedPerson);

            AddressBook actualAddressBook = TestUtil.clone(addressBook);

            Command command = createAddTagCommand(targetVisibleIndex, tagSet, actualAddressBook, displayList);
            assertCommandBehaviour(command, expectedMessage, expectedAddressBook, actualAddressBook);
        } catch (IllegalValueException ive) {
            return;
        }
    }
}
