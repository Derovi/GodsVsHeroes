package by.dero.gvh.model.annotations;

import org.bukkit.potion.PotionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PotionItem {
    PotionType potionType();
}
