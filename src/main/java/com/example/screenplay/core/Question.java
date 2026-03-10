package com.example.screenplay.core;

import com.example.screenplay.actors.Actor;

public interface Question<T> {
    T answeredBy(Actor actor) throws Exception;
}
