package net.blossom.data;

import org.jetbrains.annotations.NotNull;

interface DataTypes {

    @NotNull DataType<Double> HEALTH = DataTypeImpl.register("health", Double.class, 20.0);
    @NotNull DataType<Double> MAX_HEALTH = DataTypeImpl.register("max_health", Double.class, 20.0);
    @NotNull DataType<Integer> LEVEL = DataTypeImpl.register("level", Integer.class, 0);
    @NotNull DataType<Float> EXPERIENCE = DataTypeImpl.register("experience", Float.class, 0.0f);
    @NotNull DataType<Double> DAMAGE = DataTypeImpl.register("damage", Double.class, 0.0);
    @NotNull DataType<Float> ATTACK_SPEED = DataTypeImpl.register("attack_speed", Float.class, 0.0f);

}
