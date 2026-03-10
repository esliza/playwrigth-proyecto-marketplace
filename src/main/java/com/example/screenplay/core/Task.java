package com.example.screenplay.core;

import com.example.screenplay.actors.Actor;

public interface Task {
    void performAs(Actor actor) throws Exception;
}
