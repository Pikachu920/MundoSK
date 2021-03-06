package com.pie.tlatoani.WorldBorder;

import org.bukkit.World;
import org.bukkit.WorldBorder;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprSizeOfBorder extends SimpleExpression<Double>{
	private Expression<World> border;
	private Expression<Timespan> seconds;

	@Override
	public Class<? extends Double> getReturnType() {
		// TODO Auto-generated method stub
		return Double.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		// TODO Auto-generated method stub
		border = (Expression<World>) expr[0];
		seconds = (Expression<Timespan>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected Double[] get(Event arg0) {
		// TODO Auto-generated method stub
		WorldBorder b = border.getSingle(arg0).getWorldBorder();
		return new Double[]{ b.getSize()};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			if (seconds != null) {
				UtilBorderManager.changeSize(border.getSingle(arg0), ((Number)delta[0]).doubleValue(), new Long(seconds.getSingle(arg0).getMilliSeconds()/1000).doubleValue());
				border.getSingle(arg0).getWorldBorder().setSize(((Number)delta[0]).doubleValue(), seconds.getSingle(arg0).getMilliSeconds()/1000);
			} else {
				border.getSingle(arg0).getWorldBorder().setSize(((Number)delta[0]).doubleValue());
			}
		}
		if (mode == ChangeMode.ADD) {
			if (seconds != null) {
				UtilBorderManager.changeSize(border.getSingle(arg0), ((Number)delta[0]).doubleValue() + border.getSingle(arg0).getWorldBorder().getSize(), new Long(seconds.getSingle(arg0).getMilliSeconds()/1000).doubleValue());
				border.getSingle(arg0).getWorldBorder().setSize(((Number)delta[0]).doubleValue() + border.getSingle(arg0).getWorldBorder().getSize(), seconds.getSingle(arg0).getMilliSeconds()/1000);
			} else {
				border.getSingle(arg0).getWorldBorder().setSize(((Number)delta[0]).doubleValue() + border.getSingle(arg0).getWorldBorder().getSize());
			}
		}
		if (mode == ChangeMode.REMOVE) {
			if (seconds != null) {
				UtilBorderManager.changeSize(border.getSingle(arg0), border.getSingle(arg0).getWorldBorder().getSize() - ((Number)delta[0]).doubleValue(), new Long(seconds.getSingle(arg0).getMilliSeconds()/1000).doubleValue());
				border.getSingle(arg0).getWorldBorder().setSize(border.getSingle(arg0).getWorldBorder().getSize() - ((Number)delta[0]).doubleValue(), seconds.getSingle(arg0).getMilliSeconds()/1000);
			} else {
				border.getSingle(arg0).getWorldBorder().setSize(border.getSingle(arg0).getWorldBorder().getSize() - ((Number)delta[0]).doubleValue());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Number.class);
		}
		if (mode == ChangeMode.ADD) {
			return CollectionUtils.array(Number.class);
		}
		if (mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(Number.class);
		}
		return null;
	}

}