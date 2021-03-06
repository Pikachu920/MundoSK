package com.pie.tlatoani.Throwable;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffPrintStackTrace extends Effect{
	private Expression<Throwable> thr;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		thr = (Expression<Throwable>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "print stack trace";
	}

	@Override
	protected void execute(Event arg0) {
		thr.getSingle(arg0).printStackTrace();
	}

}