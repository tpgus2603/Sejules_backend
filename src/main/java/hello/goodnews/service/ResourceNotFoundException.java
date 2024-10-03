package hello.goodnews.service;

 class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

 class DuplicateScrapException extends RuntimeException {
    public DuplicateScrapException(String message) {
        super(message);
    }
}

class InvalidScrapException extends RuntimeException {
    public InvalidScrapException(String message) {
        super(message);
    }
}
