package io.github.Spyfall.ecs.components;

public class ButtonComponent {
    public ButtonEnum type;
    public Runnable callback;
    public enum ButtonEnum {
        JOIN_LOBBY,
        CREATE_LOBBY,
        TUTORIAL
    }

    public ButtonComponent(ButtonEnum type, Runnable callback){
        this.type = type;
        this.callback = callback;
    }

    public void onPressed(){
        if(callback != null){
            callback.run();
        }
    }
}
