package net.blossom.data;

import net.blossom.utils.UnicodeCharacters;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

interface DataTypes {

    @NotNull DataType<Float> HEALTH = DataTypeImpl.register(new DataTypeImpl<>(
            "health", Float.class, 20.0f, false, UnicodeCharacters.HEART_ICON, NamedTextColor.RED, "Amount of health an entity has. If this number reaches zero, the entity dies."
    ));
    @NotNull DataType<Float> MAX_HEALTH = DataTypeImpl.register(new DataTypeImpl<>(
            "max_health", Float.class, 20.0f, true, UnicodeCharacters.HEART_ICON, NamedTextColor.RED, "Maximum amount of health an entity can have."
    ));
    @NotNull DataType<Integer> LEVEL = DataTypeImpl.register(new DataTypeImpl<>(
            "level", Integer.class, 0,false, UnicodeCharacters.LEVEL_ICON, NamedTextColor.GOLD, "Level of a player. Levelling up unlocks more perks and abilities."
    ));
    @NotNull DataType<Float> EXPERIENCE = DataTypeImpl.register(new DataTypeImpl<>(
            "experience", Float.class, 0.0f, false, UnicodeCharacters.EXPERIENCE_ICON, NamedTextColor.GOLD, "Amount of experience a player has. Experience is gained by killing mobs and completing quests etc."
    ));
    @NotNull DataType<Double> DAMAGE = DataTypeImpl.register(new DataTypeImpl<>(
            "damage", Double.class, 0.0, true, UnicodeCharacters.DAMAGE_ICON, NamedTextColor.RED, "Amount of damage a weapon/entity deals."
    ));
    @NotNull DataType<Float> ATTACK_SPEED = DataTypeImpl.register(new DataTypeImpl<>(
            "attack_speed", Float.class, 0.0f, true, UnicodeCharacters.ATTACK_SPEED_ICON, NamedTextColor.GOLD, "Attack speed of a weapon/entity. The higher the number the more hits per second (HPS) the weapon/entity can do."
    ));
    @NotNull DataType<Integer> BREAKING_SPEED = DataTypeImpl.register(new DataTypeImpl<>(
            "breaking_speed", Integer.class, 0, true, UnicodeCharacters.BREAKING_SPEED_ICON, NamedTextColor.GOLD, "Breaking speed of a tool. The higher the number the faster the tool can break blocks."
    ));

    @NotNull DataType<Float> DEFENSE = DataTypeImpl.register(new DataTypeImpl<>(
            "defense", Float.class, 0.0f, true, UnicodeCharacters.DEFENSE_ICON, NamedTextColor.GOLD, "Amount of defense a player has. The higher the number the less damage a player takes."
    ));

    @NotNull DataType<Float> CRITICAL_CHANCE = DataTypeImpl.register(new DataTypeImpl<>(
            "critical_chance", Float.class, 0.0f, true, UnicodeCharacters.CRITICAL_CHANCE_ICON, NamedTextColor.GOLD, "Chance of a critical hit. The higher the number the more likely a critical hit will occur."
    ));

    @NotNull DataType<Float> CRITICAL_MULTIPLIER = DataTypeImpl.register(new DataTypeImpl<>(
            "critical_multiplier", Float.class, 0.0f, true, UnicodeCharacters.CRITICAL_MULTIPLIER_ICON, NamedTextColor.GOLD, "Multiplier of a critical hit. The higher the number the more damage a critical hit will do."
    ));

    @NotNull DataType<Float> STRENGTH = DataTypeImpl.register(new DataTypeImpl<>(
            "strength", Float.class, 0.0f, true, UnicodeCharacters.STRENGTH_ICON, NamedTextColor.GOLD, "Strength of a player. The higher the number the more damage a player will do."
    ));
}
