package meowmel.pollution.client.entity.dragon.model;

import meowmel.pollution.client.entity.dragon.model.util.Segment;

public interface ISegmentedPart {
    void setupSegments(Segment[] segments);

    void renderSegments(float scale);
}

