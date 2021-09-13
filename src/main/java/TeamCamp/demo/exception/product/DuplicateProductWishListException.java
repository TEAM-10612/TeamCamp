package TeamCamp.demo.exception.product;

public class DuplicateProductWishListException extends RuntimeException{
    public DuplicateProductWishListException(String message) {
        super(message);
    }
}
