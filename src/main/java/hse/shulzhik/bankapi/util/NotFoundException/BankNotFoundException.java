package hse.shulzhik.bankapi.util.NotFoundException;

public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(String message){
        super(message);
    }
}
