package by.dero.gvh.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class Customization {
    @Getter
    @Setter
    @SerializedName("_id")
    private String name;

    public Customization(String name) {
        this.name = name;
    }

    @Getter
    @Setter
    private boolean enabled = false;
}
