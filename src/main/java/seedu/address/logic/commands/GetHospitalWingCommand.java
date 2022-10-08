package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.HospitalWingContainsKeywordsPredicate;
import seedu.address.model.person.PatientType;
import seedu.address.model.person.PatientTypePredicate;
import seedu.address.model.person.Person;

/**
 * Finds and lists all patients within a hospital wing.
 * Keyword matching is case insensitive.
 */
public class GetHospitalWingCommand extends Command {

    public static final String COMMAND_WORD = "get";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all patients within the same hospital wing "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " south north";

    private final HospitalWingContainsKeywordsPredicate predicate;

    public GetHospitalWingCommand(HospitalWingContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        PatientTypePredicate patientTypePredicate = new PatientTypePredicate(PatientType.PatientTypes.INPATIENT);
        Predicate<Person> inPatientHospitalWingPredicate = patientTypePredicate.and(predicate);
        model.updateFilteredPersonList(inPatientHospitalWingPredicate);

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof GetHospitalWingCommand // instanceof handles nulls
                && predicate.equals(((GetHospitalWingCommand) other).predicate)); // state check
    }
}
