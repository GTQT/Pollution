package meowmel.pollution.client.entity.dragon.model;

import static meowmel.pollution.client.entity.dragon.model.BuiltinFactory.HORN_THICK;
import static meowmel.pollution.client.entity.dragon.model.DragonModelContracts.TAIL_SIZE;
import static meowmel.pollution.client.entity.dragon.model.util.MathX.DEGREES_TO_RADIANS;

public interface ModelMagic {
    float HALF_TAIL_SIZE = 0.5F * TAIL_SIZE;
    float TAIL_HORN_OFFSET = -0.5F * HORN_THICK;
    float TAIL_HORN_ROT_X = -15F * DEGREES_TO_RADIANS;
    float TAIL_HORN_ROT_Y = 35F * DEGREES_TO_RADIANS;
}

