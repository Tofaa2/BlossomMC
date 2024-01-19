package net.blossom.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureDepends {

    Class<? extends Feature>[] value();

}
