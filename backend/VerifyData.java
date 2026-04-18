/**
 * Validates the state of the operations layer before database actions are taken.
 *
 * type 1 → AccountOperations   : an Account must be set
 * type 2 → TransactionOperations: a TransactionGroup must be set
 * type 3 → CategoryOperations  : a Category must be set
 */
public class VerifyData {

    private Controller controllerObj;

    public VerifyData(Controller controller) {
        this.controllerObj = controller;
    }

    /**
     * Checks that the required operations object is populated for the given type.
     *
     * @param type 1 = account, 2 = transaction group, 3 = category
     * @return true if the relevant operations layer has its required object set
     */
    public boolean verifyData(int type) {
        return switch (type) {
            case 1 -> controllerObj.getAccOperations().getAccount() != null;
            case 2 -> controllerObj.getTransOperations().getTransactionGroup() != null;
            case 3 -> controllerObj.getCategoryOperations().getCategory() != null;
            default -> false;
        };
    }
}
