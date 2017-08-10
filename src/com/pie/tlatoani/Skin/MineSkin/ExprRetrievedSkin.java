package com.pie.tlatoani.Skin.MineSkin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.io.File;

/**
 * Created by Tlatoani on 5/6/17.
 * This expression should always be evaluated in async
 */
public class ExprRetrievedSkin extends SimpleExpression<Skin> {
    private Expression<String> stringExpr;
    private Expression<OfflinePlayer> offlinePlayerExpr;
    private RetrieveMode mode;

    public enum RetrieveMode {
        FILE, URL, OFFLINE_PLAYER
    }

    private String getRawString(Event event) {
        switch (mode) {
            case FILE: return MineSkinClient.rawStringFromFile(new File(stringExpr.getSingle(event)));
            case URL: return MineSkinClient.rawStringFromURL(stringExpr.getSingle(event));
            case OFFLINE_PLAYER: return MineSkinClient.rawStringFromUUID(offlinePlayerExpr.getSingle(event).getUniqueId());
        }
        throw new IllegalStateException("RetrieveMode = " + mode);
    }

    @Override
    protected Skin[] get(Event event) {
        String raw = getRawString(event);
        return new Skin[]{MineSkinClient.fromRawString(raw)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        switch (mode) {
            case FILE: return "retrieved skin from file " + stringExpr;
            case URL: return "retrieved skin from url " + stringExpr;
            case OFFLINE_PLAYER: return "retrieved skin of " + offlinePlayerExpr;
        }
        throw new IllegalStateException("RetrieveMode = " + mode);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mode = RetrieveMode.values()[parseResult.mark];
        stringExpr = (Expression<String>) expressions[0];
        offlinePlayerExpr = (Expression<OfflinePlayer>) expressions[1];
        return true;
    }
}