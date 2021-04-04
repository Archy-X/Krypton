package com.archyx.krypton.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MaterialUtil {

    private final static List<Material> materials = new ArrayList<>();

    static {
        if (XMaterial.getVersion() >= 12) {
            for (Material material : Material.values()) {
                if (material.isItem()) {
                    materials.add(material);
                }
            }
        } else {
            materials.add(Material.STONE);
            materials.add(Material.DIRT);
            materials.add(Material.GRASS);
            materials.add(Material.APPLE);
            materials.add(Material.BEACON);
            materials.add(Material.ARROW);
            materials.add(Material.BONE);
            materials.add(Material.BLAZE_POWDER);
            materials.add(Material.BLAZE_ROD);
            materials.add(Material.BOWL);
            materials.add(Material.BOW);
            materials.add(Material.BUCKET);
            materials.add(Material.CACTUS);
            materials.add(Material.CLAY);
            materials.add(Material.COOKIE);
            materials.add(Material.DIAMOND);
            materials.add(Material.COOKED_BEEF);
            materials.add(Material.COOKED_CHICKEN);
            materials.add(Material.COOKED_MUTTON);
            materials.add(Material.COOKED_RABBIT);
            materials.add(Material.DISPENSER);
            materials.add(Material.FEATHER);
            materials.add(Material.FISHING_ROD);
            materials.add(Material.FLINT);
            materials.add(Material.GLASS);
            materials.add(Material.GRAVEL);
            materials.add(Material.ICE);
            materials.add(Material.IRON_ORE);
            materials.add(Material.LADDER);
            materials.add(Material.LEATHER);
            materials.add(Material.LEVER);
            materials.add(Material.NAME_TAG);
            materials.add(Material.SAND);
            materials.add(Material.SANDSTONE);
            materials.add(Material.SEA_LANTERN);
            materials.add(Material.STICK);
            materials.add(Material.STRING);
        }
    }

    public static List<Material> getValidMaterials() {
        return materials;
    }

}
