package hse.shulzhik.bankapi.util.NotFoundException;

public class DepositNotFoundException extends RuntimeException {
    public DepositNotFoundException(String message) {
        super(message);
    }
}
