package keqing.pollution.common.block.blocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import static keqing.pollution.common.CommonProxy.Pollution_TAB;

public class TestBlock extends Block{
    public TestBlock() {
        super(Material.IRON);
        this.setResistance(10.0F);
        this.disableStats();
        this.setRegistryName("pollution", "test_block");
        this.setCreativeTab(Pollution_TAB);
        this.setTranslationKey("test_block");
    }


}
