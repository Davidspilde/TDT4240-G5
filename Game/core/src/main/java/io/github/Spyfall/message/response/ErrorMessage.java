package io.github.Spyfall.message.response;

<<<<<<< HEAD
public class ErrorMessage extends ResponseMessage {

    private String message;

    public ErrorMessage() {

=======
public class ErrorMessage extends ResponseMessage{
    private String event;
    private String message;

    public ErrorMessage() {
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
>>>>>>> main
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
<<<<<<< HEAD

}
=======
} 
>>>>>>> main
