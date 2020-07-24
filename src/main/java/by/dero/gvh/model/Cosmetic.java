package by.dero.gvh.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class Cosmetic {
    @Getter
    @Setter
    @SerializedName("_id")
    private String name;

    public Cosmetic(String name) {
        this.name = name;
    }

    @Getter
    @Setter
    private boolean enabled = false;
}
