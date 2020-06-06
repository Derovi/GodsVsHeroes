package by.dero.gvh;

public class Minigame {
    public enum State {
        IN_GAME, WAITING, PREPARING
    }

    public State getState() {
        return state;
    }

    private State state;
}
