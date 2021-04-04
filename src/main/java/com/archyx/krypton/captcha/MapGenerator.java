package com.archyx.krypton.captcha;

import com.archyx.krypton.configuration.Option;
import com.archyx.krypton.configuration.OptionL;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import java.util.Random;

public class MapGenerator {

    private final Random r = new Random();

    @SuppressWarnings("deprecation")
    public ItemStack generateMap(Player player, String code) {
        MapView view = Bukkit.createMap(player.getWorld());
        for (MapRenderer renderer : view.getRenderers()) {
            view.removeRenderer(renderer);
        }
        int[] pos = generatePosition();
        view.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView map, MapCanvas canvas, Player player) {
                canvas.drawText(pos[0], pos[1], MinecraftFont.Font, code);
            }
        });
        ItemStack item;
        if (XMaterial.isNewVersion()) {
            item = new ItemStack(Material.FILLED_MAP, 1);
            MapMeta meta = (MapMeta) item.getItemMeta();
            if (meta != null) {
                meta.setMapView(view);
                item.setItemMeta(meta);
            }
        }
        else {
            try {
                short id = (short) view.getClass().getDeclaredMethod("getId").invoke(view);
                item = new ItemStack(Material.MAP, 1, id);
            }
            catch (Exception e) {
                item = new ItemStack(Material.MAP, 1);
                e.printStackTrace();
            }
        }
        player.sendMap(view);
        return item;
    }

    public String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < OptionL.getInt(Option.MAP_CODE_LENGTH); i++) {
            if (OptionL.getBoolean(Option.MAP_USE_NUMBERS) && OptionL.getBoolean(Option.MAP_USE_CAPITAL_LETTERS)) {
                int num = r.nextInt(3);
                switch (num) {
                    case 0:
                        code.append((char) (r.nextInt(26) + 'a'));
                        break;
                    case 1:
                        code.append((char) (r.nextInt(26) + 'A'));
                        break;
                    case 2:
                        code.append(r.nextInt(10));
                        break;
                }
            }
            else if (OptionL.getBoolean(Option.MAP_USE_CAPITAL_LETTERS)) {
                int num = r.nextInt(2);
                switch (num) {
                    case 0:
                        code.append((char) (r.nextInt(26) + 'a'));
                        break;
                    case 1:
                        code.append((char) (r.nextInt(26) + 'A'));
                        break;
                }
            }
            else if (OptionL.getBoolean(Option.MAP_USE_NUMBERS)) {
                int num = r.nextInt(2);
                switch (num) {
                    case 0:
                        code.append((char) (r.nextInt(26) + 'a'));
                        break;
                    case 1:
                        code.append(r.nextInt(10));
                        break;
                }
            }
            else {
                code.append((char) (r.nextInt(26) + 'a'));
            }
        }
        return code.toString();
    }

    private int[] generatePosition() {
        int length = OptionL.getInt(Option.MAP_CODE_LENGTH);
        if (length > 20) {
            length = 20;
        }
        return new int[] {r.nextInt(80 - (length * 4) + 1) + 2, r.nextInt(80) + 5};
    }

}
