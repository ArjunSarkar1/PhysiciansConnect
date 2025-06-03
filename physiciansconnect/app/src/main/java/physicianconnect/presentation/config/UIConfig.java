package physicianconnect.presentation.config;

public final class UIConfig {
    // ─────────── Button & Label Text ───────────
    public static final String LOGIN_BUTTON_TEXT            = "Login";
    public static final String LOGOUT_BUTTON_TEXT           = "Logout";
    public static final String SAVE_BUTTON_TEXT             = "Save";
    public static final String CANCEL_BUTTON_TEXT           = "Cancel";
    public static final String ADD_APPOINTMENT_BUTTON_TEXT  = "Add Appointment";
    public static final String VIEW_APPOINTMENTS_BUTTON_TEXT= "View Appointments";
    public static final String PRESCRIBE_MEDICINE_BUTTON    = "Prescribe";
    public static final String CREATE_REFERRAL_BUTTON_TEXT  = "Create Referral";

    // ─────────── Dialog Titles ───────────
    public static final String LOGIN_DIALOG_TITLE           = "Physician Login";
    public static final String ADD_APPOINTMENT_DIALOG_TITLE = "Create New Appointment";
    public static final String VIEW_APPOINTMENT_DIALOG_TITLE= "Appointment Details";
    public static final String PRESCRIBE_MEDICINE_TITLE     = "New Prescription";
    public static final String REFERRAL_DIALOG_TITLE        = "New Referral";

    // ─────────── Field Labels ───────────
    public static final String PATIENT_NAME_LABEL           = "Patient Name:";
    public static final String DATE_LABEL                   = "Date:";
    public static final String TIME_LABEL                   = "Time:";
    public static final String NOTES_LABEL                  = "Notes:";
    public static final String MEDICATION_NAME_LABEL        = "Medication:";
    public static final String DOSAGE_LABEL                 = "Dosage:";
    public static final String START_DATE_LABEL             = "Start Date:";
    public static final String DURATION_LABEL               = "Duration (days):";
    public static final String REFERRAL_REASON_LABEL        = "Reason for Referral:";
    public static final String REFERRAL_TO_LABEL            = "Refer To (Physician ID):";
    public static final String USER_EMAIL_LABEL             = "Email:";
    public static final String USER_PASSWORD_LABEL          = "Password:";

    // ─────────── Error Messages ───────────
    public static final String ERROR_REQUIRED_FIELD         = "All fields are required.";
    public static final String ERROR_INVALID_DATE           = "Please enter a valid date.";
    public static final String ERROR_INVALID_TIME           = "Please enter a valid time.";
    public static final String ERROR_INVALID_NAME           = "Name cannot be empty.";
    public static final String ERROR_INVALID_EMAIL          = "Enter a valid email address.";
    public static final String ERROR_INVALID_DOSAGE         = "Dosage must be non-empty.";
    public static final String ERROR_INVALID_DURATION       = "Duration must be a positive integer.";
    public static final String ERROR_INVALID_REFERRAL_ID    = "Enter a valid physician ID for referral.";
    public static final String ERROR_LOGIN_FAILED           = "Login failed. Check credentials.";

    // ─────────── Miscellaneous ───────────
    public static final String APP_TITLE                    = "PhysicianConnect";
    public static final String LOADING_MESSAGE              = "Loading...";
    public static final String NO_RECORDS_FOUND             = "No records to display.";

    // Prevent instantiation
    private UIConfig() { }
}