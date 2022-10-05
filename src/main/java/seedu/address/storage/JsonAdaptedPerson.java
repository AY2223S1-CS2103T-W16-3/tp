package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Email;
import seedu.address.model.person.FloorNumber;
import seedu.address.model.person.HospitalWing;
import seedu.address.model.person.Name;
import seedu.address.model.person.NextOfKin;
import seedu.address.model.person.PatientType;
import seedu.address.model.person.PatientType.PatientTypes;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.WardNumber;
import seedu.address.model.tag.Medication;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String nextOfKin;
    private final String patientType;
    private final String hospitalWing;
    private final String floorNumber;
    private final String wardNumber;
    private final List<JsonAdaptedMedication> medications = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
                             @JsonProperty("email") String email, @JsonProperty("nextOfKin") String nextOfKin,
                             @JsonProperty("patientType") String patientType,
                             @JsonProperty("hospitalWing") String hospitalWing,
                             @JsonProperty("floorNumber") String floorNumber,
                             @JsonProperty("wardNumber") String wardNumber,
                             @JsonProperty("medications") List<JsonAdaptedMedication> medications) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nextOfKin = nextOfKin;
        this.patientType = patientType;
        this.hospitalWing = hospitalWing;
        this.floorNumber = floorNumber;
        this.wardNumber = wardNumber;
        if (medications != null) {
            this.medications.addAll(medications);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        nextOfKin = source.getNextOfKin().value;
        patientType = source.getPatientType().value.name();
        if (source.getHospitalWing().isPresent()) {
            hospitalWing = source.getHospitalWing().get().value;
        } else {
            hospitalWing = null;
        }
        if (source.getFloorNumber().isPresent()) {
            floorNumber = source.getFloorNumber().get().value.toString();
        } else {
            floorNumber = null;
        }
        if (source.getWardNumber().isPresent()) {
            wardNumber = source.getWardNumber().get().value.toString();
        } else {
            wardNumber = null;
        }
        medications.addAll(source.getMedications().stream()
                .map(JsonAdaptedMedication::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Medication> personMedications = new ArrayList<>();
        for (JsonAdaptedMedication medication : medications) {
            personMedications.add(medication.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (nextOfKin == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, NextOfKin.class.getSimpleName()));
        }
        if (!NextOfKin.isValidNextOfKin(nextOfKin)) {
            throw new IllegalValueException(NextOfKin.MESSAGE_CONSTRAINTS);
        }
        final NextOfKin modelNextOfKin = new NextOfKin(nextOfKin);

        if (patientType == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, PatientType.class.getSimpleName()));
        }
        PatientTypes pt = PatientTypes.parsePatientType(patientType);
        if (pt == null) {
            throw new IllegalValueException(NextOfKin.MESSAGE_CONSTRAINTS);
        }
        final PatientType modelPatientType = new PatientType(pt);

        if (modelPatientType.isInpatient() && hospitalWing == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    HospitalWing.class.getSimpleName()));
        }
        if (!modelPatientType.isInpatient() && hospitalWing != null) {
            throw new IllegalValueException(String.format(PatientType.DEPENDENCY_CONSTRAINTS,
                    HospitalWing.class.getSimpleName()));
        }
        if (hospitalWing != null && !HospitalWing.isValidHospitalWing(hospitalWing)) {
            throw new IllegalValueException(HospitalWing.MESSAGE_CONSTRAINTS);
        }
        final HospitalWing modelHospitalWing = hospitalWing == null ? null : new HospitalWing(hospitalWing);


        if (modelPatientType.isInpatient() && floorNumber == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    HospitalWing.class.getSimpleName()));
        }
        if (!modelPatientType.isInpatient() && floorNumber != null) {
            throw new IllegalValueException(String.format(PatientType.DEPENDENCY_CONSTRAINTS,
                    FloorNumber.class.getSimpleName()));
        }
        Integer fN = null;
        if (floorNumber != null) {
            try {
                fN = Integer.valueOf(floorNumber);
            } catch (NumberFormatException nfe) {
                throw new IllegalValueException(FloorNumber.MESSAGE_CONSTRAINTS);
            }
            if (!FloorNumber.isValidFloorNumber(fN)) {
                throw new IllegalValueException(FloorNumber.MESSAGE_CONSTRAINTS);
            }
        }
        final FloorNumber modelFloorNumber = floorNumber == null ? null : new FloorNumber(fN);


        if (modelPatientType.isInpatient() && wardNumber == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    WardNumber.class.getSimpleName()));
        }
        if (!modelPatientType.isInpatient() && wardNumber != null) {
            throw new IllegalValueException(String.format(PatientType.DEPENDENCY_CONSTRAINTS,
                    WardNumber.class.getSimpleName()));
        }
        Integer wN = null;
        if (wardNumber != null) {
            try {
                wN = Integer.valueOf(wardNumber);
            } catch (NumberFormatException nfe) {
                throw new IllegalValueException(WardNumber.MESSAGE_CONSTRAINTS);
            }
            if (!WardNumber.isValidWardNumber(wN)) {
                throw new IllegalValueException(WardNumber.MESSAGE_CONSTRAINTS);
            }
        }
        final WardNumber modelWardNumber = wardNumber == null ? null : new WardNumber(wN);

        final Set<Medication> modelMedication = new HashSet<>(personMedications);

        return new Person(modelName, modelPhone, modelEmail, modelNextOfKin, modelPatientType,
                modelHospitalWing, modelFloorNumber, modelWardNumber, modelMedication);
    }

}
