package keqing.pollution;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class LateMixin implements ILateMixinLoader {

	@Override
	public List<String> getMixinConfigs() {
		return Collections.singletonList("mixins.pollution_late.json");
	}

}