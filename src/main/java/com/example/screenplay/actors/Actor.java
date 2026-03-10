package com.example.screenplay.actors;

import com.example.screenplay.core.Ability;
import com.example.screenplay.core.Task;
import java.util.HashMap;
import java.util.Map;

public class Actor {
    private final String name;
    private final Map<Class<?>, Object> abilities = new HashMap<>();

    public Actor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public <T extends Ability> void can(T ability) {
        abilities.put(ability.getClass(), ability);
    }

    public <T> T abilityTo(Class<T> abilityClass) {
        return abilityClass.cast(abilities.get(abilityClass));
    }

    public void attemptsTo(Task... tasks) throws Exception {
        for (Task t : tasks) {
            t.performAs(this);
        }
    }
}
