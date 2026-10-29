package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_NOTE = new Prefix("nt/");
    public static final Prefix PREFIX_FEE = new Prefix("fee/");
    public static final Prefix PREFIX_DUE_DATE = new Prefix("due/");

    /** Every prefix that add and edit accept, apart from {@code PREFIX_NOTE}. */
    public static final Prefix[] PREFIXES_EXCEPT_NOTE = {PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
        PREFIX_FEE, PREFIX_DUE_DATE, PREFIX_TAG};

}
