package hse.shulzhik.bankapi.util.NotFoundException;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
