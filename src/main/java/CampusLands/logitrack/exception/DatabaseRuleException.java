package CampusLands.logitrack.exception;

public class DatabaseRuleException extends RuntimeException{
    public DatabaseRuleException(String mensaje){
        super(mensaje);
    }
}
