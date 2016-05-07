package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.pie.tlatoani.Json.API.Json;
import com.pie.tlatoani.Json.API.JsonObject;
import com.pie.tlatoani.Json.API.JsonReader;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Tlatoani on 5/4/16.
 */
public class ExprJsonObjectOfPacket extends SimpleExpression<JsonObject> {
    private Method getObjects = null;
    private PacketInfoGetter getFunction;
    private PacketInfoSetter setFunction;
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private Class aClass;

    public static Map<String, PacketInfoGetter> getFunctionMap = new HashMap<String, PacketInfoGetter>();
    public static Map<String, PacketInfoSetter> setFunctionMap = new HashMap<String, PacketInfoSetter>();

    static {
        //Getters
        getFunctionMap.put("chatcomponent", new PacketInfoGetter() {
            @Override
            public JsonObject apply(PacketContainer packet, Integer index) {
                Mundo.debug(this, "Packet :" + packet);
                Mundo.debug(this, "ChatComponents :" + packet.getChatComponents());
                WrappedChatComponent chatComponent = packet.getChatComponents().readSafely(index);
                String fromjson = chatComponent.getJson();
                Mundo.debug(this, "Fromjson: " + fromjson);
                JsonReader jsonReader = Json.createReader(new StringReader(fromjson));
                JsonObject tojson = jsonReader.readObject();
                Mundo.debug(this, "Tojson " + tojson);
                return tojson;
            }
        });

        //Setters
        setFunctionMap.put("chatcomponent", new PacketInfoSetter() {
            @Override
            public void apply(PacketContainer packet, Integer index, JsonObject value) {
                WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(value.toString());
                packet.getChatComponents().writeSafely(index, chatComponent);
            }
        });
    }

    @FunctionalInterface
    public interface PacketInfoGetter {
        public JsonObject apply(PacketContainer packet, Integer index);

    }

    @FunctionalInterface
    public interface PacketInfoSetter {
        public void apply(PacketContainer packet, Integer index, JsonObject value);

    }

    @Override
    protected JsonObject[] get(Event event) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        Mundo.debug(this, "Packet before calling function :" + packet);
        JsonObject result = getFunction.apply(packet, index.getSingle(event).intValue());
        return new JsonObject[]{result};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<JsonObject> getReturnType() {
        return JsonObject.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "%string% pjson %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        String string;
        if (expressions[0] instanceof Literal<?>) {
            string = ((Literal<String>) expressions[0]).getSingle();
        } else if (expressions[0] instanceof VariableString) {
            String fullstring = ((VariableString) expressions[0]).toString();
            string = fullstring.substring(1, fullstring.length() - 1);
        } else {
            Skript.error("The string '" + expressions[0] + "' is not a literal string! Only literal strings can be used in the pjson expression!");
            return false;
        }
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        if (getFunctionMap.containsKey(string.toLowerCase())) {
            getFunction = getFunctionMap.get(string.toLowerCase());
            setFunction = setFunctionMap.get(string.toLowerCase());
            return true;
        } else {
            Skript.error("The string " + string + " is not a valid packetinfo!");
            return false;
        }
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        PacketContainer packet = packetContainerExpression.getSingle(event);
        Mundo.debug(this, "Packet before calling function :" + packet);
        setFunction.apply(packet, index.getSingle(event).intValue(), ((JsonObject) delta[0]));
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(JsonObject.class);
        }
        return null;
    }
}