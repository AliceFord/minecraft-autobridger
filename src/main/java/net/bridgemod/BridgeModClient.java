package net.bridgemod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

class Bridge extends TimerTask {
    private Robot robot;
    private MinecraftClient client;
    
    Bridge(Robot robot, MinecraftClient client) {
        this.robot = robot;
        this.client = client;
    }
    
    @Override
    public void run() {
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        Timer timer = new Timer();
        if (BridgeModClient.bridge) {
            timer.schedule(new Bridge2(robot, client), 200);
        } else {
            client.player.sendMessage(new LiteralText("Ending Bridging"), false);
        }
    }
}

class Bridge2 extends TimerTask {
    private Robot robot;
    private MinecraftClient client;
    
    Bridge2(Robot robot, MinecraftClient client) {
        this.robot = robot;
        this.client = client;
    }
    
    @Override
    public void run() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        Timer timer = new Timer();
        timer.schedule(new Bridge(robot, client), 100);
    }
}


public class BridgeModClient implements ClientModInitializer {
    public static Boolean bridge = false;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ss.SSS");
    private Float currentTime = Float.parseFloat(dtf.format(LocalDateTime.now()));
    private Float tempTime;
    private final Timer timer = new Timer();
    private Robot robot;
    
    static {
        System.setProperty("java.awt.headless", "false");
    }
    
    public BridgeModClient() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onInitializeClient() {
        KeyBinding j_binding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fabric-key-binding-api-v1-bridgemod.j_keybind", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.category.binding"));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (j_binding.isPressed()) {
                if ((tempTime = Float.parseFloat(dtf.format(LocalDateTime.now()))) - currentTime > 0.2) {
                    bridge = !bridge;
                    if (bridge) {
                        client.player.sendMessage(new LiteralText("Starting Bridging"), false);
                        robot.keyPress(KeyEvent.VK_S);
                        robot.keyPress(KeyEvent.VK_D);
                        timer.schedule(new Bridge(robot, client), 0);
                    } else {
                        robot.keyRelease(KeyEvent.VK_S);
                        robot.keyRelease(KeyEvent.VK_D);
                    }
                    
                    currentTime = tempTime;
                }
            }
        });
    }
}
