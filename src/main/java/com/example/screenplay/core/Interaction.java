package com.example.screenplay.core;

import com.example.screenplay.actors.Actor;

public interface Interaction {
    void performAs(Actor actor) throws Exception;
}
