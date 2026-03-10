package com.example.screenplay.questions;

import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Question;

public class PageContainsText implements Question<Boolean> {
    private final String text;

    public PageContainsText(String text) {
        this.text = text;
    }

    @Override
    public Boolean answeredBy(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        String content = up.page().content();
        return content != null && content.contains(text);
    }
}
