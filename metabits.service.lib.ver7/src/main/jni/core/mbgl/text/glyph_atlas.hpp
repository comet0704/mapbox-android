#pragma once

#include <mbgl/text/glyph.hpp>

#include <mbgl/shelf-pack.hpp>

namespace mbgl {

struct GlyphPosition {
    Rect<uint16_t> rect;
    GlyphMetrics metrics;
};

using GlyphPositionMap = std::map<GlyphID, GlyphPosition>;
using GlyphPositions = std::map<FontStackHash, GlyphPositionMap>;

class GlyphAtlas {
public:
    AlphaImage image;
    GlyphPositions positions;
};

GlyphAtlas makeGlyphAtlas(const GlyphMap&);

} // namespace mbgl
