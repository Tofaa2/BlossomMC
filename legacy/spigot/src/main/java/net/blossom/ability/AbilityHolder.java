package net.blossom.ability;

import net.blossom.actuation.FeatureTrigger;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public interface AbilityHolder {

    @Nullable Ability[] getAbilities();

    default @Nullable Ability[] getAbilities(FeatureTrigger trigger) {
        var abilities = getAbilities();
        if (abilities == null) {
            return null;
        }
        return Arrays.stream(abilities)
                .filter(ability -> ability.getTrigger() == trigger)
                .toArray(Ability[]::new);
    }

}
