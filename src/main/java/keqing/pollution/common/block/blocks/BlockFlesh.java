package keqing.pollution.common.block.blocks;

import keqing.pollution.Pollution;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * 血肉块 - 树干/树身材质
 * 柔软的肉质方块，发出湿润的声音
 */
public class BlockFlesh extends Block {

    public BlockFlesh() {
        super(Material.SPONGE); // 使用海绵材质模拟肉质手感
        setTranslationKey(Pollution.MODID + ".flesh_block");
        setRegistryName(Pollution.MODID, "flesh_block");
        setHardness(1.5F);
        setResistance(2.0F);
        setSoundType(SoundType.SLIME); // 黏滑的声音
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
}
